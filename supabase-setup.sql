-- Execute este arquivo uma vez no SQL Editor do projeto Supabase.
alter table public.usuarios
add column if not exists senha text;

alter table public.usuarios
alter column telefone drop not null;

notify pgrst, 'reload schema';

alter table public.usuarios enable row level security;

drop policy if exists usuarios_insert_public on public.usuarios;
create policy usuarios_insert_public
on public.usuarios
for insert
to public
with check (true);

drop policy if exists usuarios_select_public on public.usuarios;
create policy usuarios_select_public
on public.usuarios
for select
to public
using (true);