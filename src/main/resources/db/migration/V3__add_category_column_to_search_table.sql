ALTER TABLE public.search ADD COLUMN category CHARACTER VARYING(30) DEFAULT 'ALL';
CREATE INDEX idx_owner_id ON public.search(owner_id);