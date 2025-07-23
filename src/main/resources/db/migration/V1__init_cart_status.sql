CREATE TABLE IF NOT EXISTS cart_status (
    cart_id UUID PRIMARY KEY,                      -- Unique cart identifier
    user_id TEXT,                                  -- Optional user ID
    session_id TEXT,                               -- Optional session ID
    cart_items JSONB NOT NULL,                     -- Cart contents
    updated_timestamp TIMESTAMPTZ NOT NULL,        -- Last cart activity
    status TEXT NOT NULL,                          -- Cart status (e.g., ACTIVE, CHECKOUT_INITIATED, NOTIFICATION_INITIATED)
    notification_id TEXT,                          -- Used for tracking the notification created. Useful for sending notification cancellation
    source TEXT,                                   -- Traffic source
    experiment_variant TEXT,                       -- Experiment ID
    created_at TIMESTAMPTZ DEFAULT now(),          -- Record creation time
    expires_at TIMESTAMPTZ                         -- Optional TTL
);

-- Composite index for your query pattern
CREATE INDEX IF NOT EXISTS idx_status_updated ON cart_status (status, updated_timestamp);

-- Optional: index for session-based lookup
CREATE INDEX IF NOT EXISTS idx_session ON cart_status (session_id);

