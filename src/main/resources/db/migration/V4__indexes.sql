CREATE INDEX idx_tags_user_id_id ON tags (user_id, id);
CREATE INDEX idx_notebooks_deleted_at ON notebooks (user_id, deleted_at);