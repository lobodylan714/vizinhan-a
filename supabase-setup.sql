-- Execute este arquivo uma vez no SQL Editor do projeto Supabase.
alter table public.usuarios enable row level security;

drop policy if exists usuarios_insert_public on public.usuarios;
create policy usuarios_insert_public
on public.usuarios
for insert
to anon, authenticated
with check (true);