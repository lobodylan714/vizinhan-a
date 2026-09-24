const supabaseUrl = 'https://qgcbtcgdfydtgzmtjaxs.supabase.co';
const supabaseKey = 'sb_publishable_yLWo1rec33dEcdZ3eragcw_OUbNkAgF';

const supabaseClient = window.supabase.createClient(supabaseUrl, supabaseKey);

const TEST_USER = {
  nome: 'Usuário de Teste',
  email: 'teste@vizinhoajuda.com',
  senha: '123456'
};

const MAIN_PAGE = 'community.html';
const tabButtons = document.querySelectorAll('.tab-button');
const forms = {
  login: document.getElementById('loginForm'),
  register: document.getElementById('registerForm')
};
const messageBox = document.getElementById('message');

function showMessage(text, type = '') {
  messageBox.textContent = text;
  messageBox.className = 'message';

  if (type) {
    messageBox.classList.add(type);
  }
}

function switchTab(tab) {
  tabButtons.forEach((button) => {
    button.classList.toggle('active', button.dataset.tab === tab);
  });

  Object.entries(forms).forEach(([key, form]) => {
    form.classList.toggle('active', key === tab);
  });
}

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function handleTestUserLogin(email, password) {
  const normalizedEmail = email.trim().toLowerCase();

  if (normalizedEmail === TEST_USER.email.toLowerCase() && password === TEST_USER.senha) {
    showMessage(`Senha correta! Bem-vindo, ${TEST_USER.nome}!`, 'success');
    localStorage.setItem('vizinhoUser', JSON.stringify(TEST_USER));
    setTimeout(() => {
      window.location.href = MAIN_PAGE;
    }, 800);
    return true;
  }

  if (normalizedEmail === TEST_USER.email.toLowerCase()) {
    showMessage('Senha incorreta. Verifique a senha e tente novamente.', 'error');
  }

  return false;
}

tabButtons.forEach((button) => {
  button.addEventListener('click', () => switchTab(button.dataset.tab));
});

forms.login.addEventListener('submit', async (event) => {
  event.preventDefault();

  const email = document.getElementById('loginEmail').value.trim().toLowerCase();
  const password = document.getElementById('loginPassword').value.trim();

  if (!email || !password) {
    showMessage('Preencha e-mail e senha.', 'error');
    return;
  }

  if (!isValidEmail(email)) {
    showMessage('Digite um e-mail válido, como seuemail@gmail.com.', 'error');
    return;
  }

  if (handleTestUserLogin(email, password)) {
    return;
  }

  try {
    const { data, error } = await supabaseClient
      .from('usuarios')
      .select('*')
      .eq('email', email)
      .maybeSingle();

    if (error) throw error;

    if (!data) {
      showMessage('Usuário não encontrado. Use o usuário de teste: teste@vizinhoajuda.com / 123456', 'error');
      return;
    }

    if (data.senha !== password) {
      showMessage('Senha incorreta. Verifique a senha e tente novamente.', 'error');
      return;
    }

    localStorage.setItem('vizinhoUser', JSON.stringify({
      nome: data.nome,
      email: data.email
    }));

    showMessage(`Bem-vindo, ${data.nome}!`, 'success');
    setTimeout(() => {
      window.location.href = MAIN_PAGE;
    }, 800);
  } catch (error) {
    console.error(error);
    showMessage('Erro ao fazer login. Verifique a tabela e as regras do Supabase.', 'error');
  }
});

forms.register.addEventListener('submit', async (event) => {
  event.preventDefault();

  const nome = document.getElementById('registerName').value.trim();
  const email = document.getElementById('registerEmail').value.trim().toLowerCase();
  const password = document.getElementById('registerPassword').value.trim();

  if (!nome || !email || !password) {
    showMessage('Preencha todos os campos.', 'error');
    return;
  }

  if (!isValidEmail(email)) {
    showMessage('Digite um e-mail válido, como seuemail@gmail.com.', 'error');
    return;
  }

  if (password.length < 6) {
    showMessage('A senha precisa ter pelo menos 6 caracteres.', 'error');
    return;
  }

  if (email.toLowerCase() === TEST_USER.email.toLowerCase() && password === TEST_USER.senha) {
    showMessage('Este usuário de teste já existe no sistema.', 'error');
    return;
  }

  try {
    const { data: existingUser, error: selectError } = await supabaseClient
      .from('usuarios')
      .select('id')
      .eq('email', email)
      .maybeSingle();

    if (selectError) throw selectError;

    if (existingUser) {
      showMessage('Este e-mail já está cadastrado.', 'error');
      return;
    }

    const { data, error } = await supabaseClient.from('usuarios').insert([
      { nome, email, senha: password }
    ]).select();

    if (error) throw error;

    localStorage.setItem('vizinhoUser', JSON.stringify({ nome, email }));
    showMessage('Cadastro realizado com sucesso! Abrindo a comunidade...', 'success');
    forms.register.reset();
    setTimeout(() => {
      window.location.href = MAIN_PAGE;
    }, 800);
  } catch (error) {
    console.error(error);
    showMessage('Erro ao cadastrar. Verifique se a tabela existe e se o Supabase permite inserir dados públicos.', 'error');
  }
});

const userLogged = JSON.parse(localStorage.getItem('vizinhoUser') || 'null');
if (userLogged && window.location.pathname.endsWith('index.html')) {
  window.location.href = MAIN_PAGE;
}
