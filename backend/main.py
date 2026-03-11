"""Android↔OpenHarmony API Compatibility Portal — Backend."""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
import os

from .routers import android, openharmony, mappings, search, stats, subsystems

app = FastAPI(
    title="Android↔OpenHarmony API Compatibility Portal",
    description="Browse, search, and compare Android 11 APIs with OpenHarmony equivalents",
    version="1.0.0",
)

# CORS for frontend dev server
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register routers
app.include_router(android.router)
app.include_router(openharmony.router)
app.include_router(mappings.router)
app.include_router(search.router)
app.include_router(stats.router)
app.include_router(subsystems.router)


@app.get("/api/health")
def health():
    from .database import get_db
    with get_db() as db:
        android_count = db.execute("SELECT COUNT(*) FROM android_apis").fetchone()[0]
        oh_count = db.execute("SELECT COUNT(*) FROM oh_apis").fetchone()[0]
        mapping_count = db.execute("SELECT COUNT(*) FROM api_mappings").fetchone()[0]
    return {
        "status": "ok",
        "android_apis": android_count,
        "oh_apis": oh_count,
        "mappings": mapping_count,
    }


# Serve frontend static files if they exist
frontend_dist = os.path.join(os.path.dirname(__file__), "..", "frontend", "dist")
if os.path.isdir(frontend_dist):
    app.mount("/", StaticFiles(directory=frontend_dist, html=True), name="frontend")
