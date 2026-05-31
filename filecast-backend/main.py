import asyncio
import os
from contextlib import asynccontextmanager
from typing import Optional
import uuid

import httpx
from fastapi import FastAPI, File, Form, Header, HTTPException, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from sqlmodel import Field, Session, SQLModel, create_engine, select

CLOUDCONVERT_API_KEY = os.getenv("CLOUDCONVERT_API_KEY", "")
# Sandbox key  → use sandbox endpoint; production key → use production endpoint
CLOUDCONVERT_BASE = "https://api.sandbox.cloudconvert.com/v2"

# ── DB models (snake_case, stored in SQLite) ──────────────────────────────────

class ConversionBase(SQLModel):
    name: str
    original_format: str
    target_format: str
    size_mb: float
    date: str
    status: str = "SUCCESS"

class Conversion(ConversionBase, table=True):
    id: Optional[str] = Field(default=None, primary_key=True)
    user_id: str = ""

class ConversionCreate(BaseModel):
    name: str
    originalFormat: str
    targetFormat: str
    sizeMb: float
    date: str
    status: str = "SUCCESS"

class ConversionUpdate(BaseModel):
    name: Optional[str] = None
    originalFormat: Optional[str] = None
    targetFormat: Optional[str] = None
    sizeMb: Optional[float] = None
    date: Optional[str] = None
    status: Optional[str] = None

# camelCase response shape — matches Android ConversionDto exactly
class ConversionOut(BaseModel):
    id: str
    name: str
    originalFormat: str
    targetFormat: str
    sizeMb: float
    date: str
    status: str

def to_out(c: Conversion) -> ConversionOut:
    return ConversionOut(
        id=c.id,
        name=c.name,
        originalFormat=c.original_format,
        targetFormat=c.target_format,
        sizeMb=c.size_mb,
        date=c.date,
        status=c.status,
    )

# ── DB setup ──────────────────────────────────────────────────────────────────

DATABASE_URL = "sqlite:///./filecast.db"
engine = create_engine(DATABASE_URL, echo=False, connect_args={"check_same_thread": False})

def seed_if_empty(session: Session):
    if session.exec(select(Conversion)).first():
        return
    seeds = [
        Conversion(id=str(uuid.uuid4()), name="report.docx", original_format="DOCX",
                   target_format="PDF", size_mb=1.2, date="May 01, 2026", status="SUCCESS"),
        Conversion(id=str(uuid.uuid4()), name="photo.png", original_format="PNG",
                   target_format="JPG", size_mb=3.4, date="May 03, 2026", status="SUCCESS"),
        Conversion(id=str(uuid.uuid4()), name="audio.wav", original_format="WAV",
                   target_format="MP3", size_mb=8.7, date="May 10, 2026", status="SUCCESS"),
        Conversion(id=str(uuid.uuid4()), name="data.xlsx", original_format="XLSX",
                   target_format="PDF", size_mb=0.9, date="May 15, 2026", status="FAILED"),
    ]
    for s in seeds:
        session.add(s)
    session.commit()

@asynccontextmanager
async def lifespan(app: FastAPI):
    SQLModel.metadata.create_all(engine)
    with Session(engine) as session:
        seed_if_empty(session)
    yield

# ── App ───────────────────────────────────────────────────────────────────────

app = FastAPI(title="FileCast API", lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# ── Endpoints ─────────────────────────────────────────────────────────────────

@app.get("/conversions/", response_model=list[ConversionOut])
def get_conversions(x_authentication: Optional[str] = Header(default=None)):
    with Session(engine) as session:
        rows = session.exec(select(Conversion)).all()
        return [to_out(r) for r in rows]

@app.get("/conversions/{id}", response_model=ConversionOut)
def get_conversion(id: str, x_authentication: Optional[str] = Header(default=None)):
    with Session(engine) as session:
        row = session.get(Conversion, id)
        if not row:
            raise HTTPException(status_code=404, detail="Conversion not found")
        return to_out(row)

@app.post("/conversions/", response_model=ConversionOut, status_code=201)
def create_conversion(body: ConversionCreate, x_authentication: Optional[str] = Header(default=None)):
    with Session(engine) as session:
        row = Conversion(
            id=str(uuid.uuid4()),
            name=body.name,
            original_format=body.originalFormat,
            target_format=body.targetFormat,
            size_mb=body.sizeMb,
            date=body.date,
            status=body.status,
        )
        session.add(row)
        session.commit()
        session.refresh(row)
        return to_out(row)

@app.put("/conversions/{id}", response_model=ConversionOut)
def update_conversion(id: str, body: ConversionUpdate, x_authentication: Optional[str] = Header(default=None)):
    with Session(engine) as session:
        row = session.get(Conversion, id)
        if not row:
            raise HTTPException(status_code=404, detail="Conversion not found")
        if body.name is not None:           row.name = body.name
        if body.originalFormat is not None: row.original_format = body.originalFormat
        if body.targetFormat is not None:   row.target_format = body.targetFormat
        if body.sizeMb is not None:         row.size_mb = body.sizeMb
        if body.date is not None:           row.date = body.date
        if body.status is not None:         row.status = body.status
        session.add(row)
        session.commit()
        session.refresh(row)
        return to_out(row)

@app.delete("/conversions/{id}", status_code=204)
def delete_conversion(id: str, x_authentication: Optional[str] = Header(default=None)):
    with Session(engine) as session:
        row = session.get(Conversion, id)
        if not row:
            raise HTTPException(status_code=404, detail="Conversion not found")
        session.delete(row)
        session.commit()

# ── CloudConvert endpoint ─────────────────────────────────────────────────────

class ConvertResponse(BaseModel):
    downloadUrl: str
    fileName: str

@app.post("/convert/", response_model=ConvertResponse)
async def convert_file(
    file: UploadFile = File(...),
    targetFormat: str = Form(...),
    x_authentication: Optional[str] = Header(default=None),
):
    """
    Accepts a file + targetFormat, sends it to CloudConvert, and returns a
    temporary download URL for the converted file.  Requires the env variable
    CLOUDCONVERT_API_KEY to be set.
    """
    if not CLOUDCONVERT_API_KEY:
        raise HTTPException(status_code=503, detail="CloudConvert API key not configured")

    file_bytes = await file.read()
    original_name = file.filename or "input"
    cc_headers = {"Authorization": f"Bearer {CLOUDCONVERT_API_KEY}"}

    async with httpx.AsyncClient(timeout=120.0) as client:

        # ── Step 1: Create job ────────────────────────────────────────────────
        job_payload = {
            "tasks": {
                "upload-file":   {"operation": "import/upload"},
                "convert-file":  {
                    "operation":     "convert",
                    "input":         "upload-file",
                    "output_format": targetFormat.lower(),
                },
                "export-file":   {
                    "operation": "export/url",
                    "input":     "convert-file",
                },
            }
        }
        resp = await client.post(
            f"{CLOUDCONVERT_BASE}/jobs",
            json=job_payload,
            headers=cc_headers,
        )
        if resp.status_code != 201:
            raise HTTPException(status_code=502, detail=f"CloudConvert job creation failed: {resp.text}")

        job_data = resp.json()["data"]
        job_id   = job_data["id"]

        # ── Step 2: Upload file ───────────────────────────────────────────────
        upload_task = next(
            t for t in job_data["tasks"] if t["operation"] == "import/upload"
        )
        upload_url    = upload_task["result"]["form"]["url"]
        upload_params = upload_task["result"]["form"]["parameters"]

        # S3 requires all form fields BEFORE the file field
        upload_resp = await client.post(
            upload_url,
            data=upload_params,
            files={"file": (original_name, file_bytes, file.content_type or "application/octet-stream")},
        )
        if upload_resp.status_code not in (200, 201, 204):
            raise HTTPException(status_code=502, detail="File upload to CloudConvert failed")

        # ── Step 3: Poll job until finished ──────────────────────────────────
        for _ in range(30):          # 30 × 4 s = 2 min max
            await asyncio.sleep(4)
            poll_resp = await client.get(
                f"{CLOUDCONVERT_BASE}/jobs/{job_id}",
                headers=cc_headers,
            )
            job_data = poll_resp.json()["data"]
            status   = job_data["status"]
            if status == "finished":
                break
            if status == "error":
                raise HTTPException(status_code=502, detail="CloudConvert conversion failed")
        else:
            raise HTTPException(status_code=504, detail="CloudConvert job timed out")

        # ── Step 4: Return download URL ───────────────────────────────────────
        export_task = next(
            t for t in job_data["tasks"] if t["operation"] == "export/url"
        )
        file_info = export_task["result"]["files"][0]
        return ConvertResponse(downloadUrl=file_info["url"], fileName=file_info["filename"])
