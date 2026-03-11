"""SQLite database connection and helpers."""

import sqlite3
import os
from contextlib import contextmanager

DB_PATH = os.path.expanduser("~/android-to-openharmony-migration/database/api_compat.db")


def get_connection():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA journal_mode=WAL")
    conn.execute("PRAGMA foreign_keys=ON")
    return conn


@contextmanager
def get_db():
    conn = get_connection()
    try:
        yield conn
    finally:
        conn.close()


def rows_to_dicts(rows):
    """Convert sqlite3.Row objects to list of dicts."""
    return [dict(row) for row in rows]


def row_to_dict(row):
    """Convert single sqlite3.Row to dict."""
    return dict(row) if row else None
