"""Search endpoints using FTS5."""

from fastapi import APIRouter, Query
from typing import Optional
from ..database import get_db, rows_to_dicts

router = APIRouter(prefix="/api/search", tags=["Search"])


@router.get("")
def search_apis(
    q: str = Query(..., min_length=1),
    platform: Optional[str] = None,  # "android", "oh", or None for both
    subsystem: Optional[str] = None,
    score_min: Optional[float] = None,
    score_max: Optional[float] = None,
    limit: int = Query(50, ge=1, le=200),
):
    results = []

    with get_db() as db:
        # Prepare FTS query — escape special chars
        fts_query = q.replace('"', '').replace("'", "")

        # Search Android APIs
        if platform in (None, "android"):
            where_extra = ""
            params = []
            if subsystem:
                where_extra += " AND a.subsystem = ?"
                params.append(subsystem)
            if score_min is not None:
                where_extra += " AND a.compat_score >= ?"
                params.append(score_min)
            if score_max is not None:
                where_extra += " AND a.compat_score <= ?"
                params.append(score_max)

            android_rows = db.execute(f"""
                SELECT a.id, a.name, a.signature, a.kind, a.subsystem,
                       a.compat_score as score,
                       t.name as type_name, p.name as package_name,
                       'android' as platform
                FROM android_apis a
                JOIN android_types t ON a.type_id = t.id
                JOIN android_packages p ON t.package_id = p.id
                WHERE (a.name LIKE ? OR a.signature LIKE ?)
                {where_extra}
                ORDER BY
                    CASE WHEN a.name LIKE ? THEN 0 ELSE 1 END,
                    a.name
                LIMIT ?
            """, [f"%{fts_query}%", f"%{fts_query}%"] + params + [f"{fts_query}%", limit]).fetchall()

            for row in android_rows:
                results.append({
                    "platform": "android",
                    "id": row["id"],
                    "name": row["name"],
                    "signature": row["signature"],
                    "kind": row["kind"],
                    "type_name": row["type_name"],
                    "package_or_module": row["package_name"],
                    "subsystem": row["subsystem"],
                    "score": row["score"],
                })

        # Search OH APIs
        if platform in (None, "oh"):
            where_extra = ""
            params = []
            if subsystem:
                where_extra += " AND a.subsystem = ?"
                params.append(subsystem)

            oh_rows = db.execute(f"""
                SELECT a.id, a.name, a.signature, a.kind, a.subsystem,
                       COALESCE(t.name, '') as type_name, m.name as module_name,
                       'oh' as platform
                FROM oh_apis a
                LEFT JOIN oh_types t ON a.type_id = t.id
                JOIN oh_modules m ON a.module_id = m.id
                WHERE (a.name LIKE ? OR a.signature LIKE ?)
                {where_extra}
                ORDER BY
                    CASE WHEN a.name LIKE ? THEN 0 ELSE 1 END,
                    a.name
                LIMIT ?
            """, [f"%{fts_query}%", f"%{fts_query}%"] + params + [f"{fts_query}%", limit]).fetchall()

            for row in oh_rows:
                results.append({
                    "platform": "oh",
                    "id": row["id"],
                    "name": row["name"],
                    "signature": row["signature"],
                    "kind": row["kind"],
                    "type_name": row["type_name"],
                    "package_or_module": row["module_name"],
                    "subsystem": row["subsystem"],
                    "score": None,
                })

    return {"results": results, "query": q, "total": len(results)}


@router.get("/autocomplete")
def autocomplete(q: str = Query(..., min_length=2), limit: int = 10):
    with get_db() as db:
        # Quick name-prefix search
        android = db.execute("""
            SELECT DISTINCT name, 'android' as platform
            FROM android_apis WHERE name LIKE ? LIMIT ?
        """, (f"{q}%", limit)).fetchall()

        oh = db.execute("""
            SELECT DISTINCT name, 'oh' as platform
            FROM oh_apis WHERE name LIKE ? LIMIT ?
        """, (f"{q}%", limit)).fetchall()

        suggestions = []
        for row in android:
            suggestions.append({"name": row["name"], "platform": "android"})
        for row in oh:
            suggestions.append({"name": row["name"], "platform": "oh"})

        # Dedupe and sort
        seen = set()
        unique = []
        for s in sorted(suggestions, key=lambda x: x["name"]):
            key = (s["name"], s["platform"])
            if key not in seen:
                seen.add(key)
                unique.append(s)

        return unique[:limit * 2]
