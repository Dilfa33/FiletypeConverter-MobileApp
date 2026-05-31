# FileCast Backend

FastAPI REST API for the FileCast Android app.

## Setup

```bash
cd filecast-backend
pip install -r requirements.txt
```

## Run

```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

- Android emulator hits: `http://10.0.2.2:8000/`
- Physical device (same Wi-Fi) hits: `http://<your-LAN-IP>:8000/`

## Verify endpoints

```bash
# GET all
curl http://localhost:8000/conversions/

# GET one
curl http://localhost:8000/conversions/<id>

# POST create
curl -X POST http://localhost:8000/conversions/ \
  -H "Content-Type: application/json" \
  -d '{"name":"test.png","originalFormat":"PNG","targetFormat":"JPG","sizeMb":2.5,"date":"May 30, 2026","status":"SUCCESS"}'

# PUT update
curl -X PUT http://localhost:8000/conversions/<id> \
  -H "Content-Type: application/json" \
  -d '{"status":"FAILED"}'

# DELETE
curl -X DELETE http://localhost:8000/conversions/<id>
```

Docs available at: http://localhost:8000/docs
