/* ==========================================================
   Front-end do Controle Veicular
   Consome a API REST (mesma origem: http://localhost:8080)
   ========================================================== */

const estado = { veiculos: [], manutencoes: [], documentos: [] };

/* ---------- Utilidades ---------- */

const $ = (id) => document.getElementById(id);

// Cria texto seguro para inserir em HTML (evita injeção de código)
function esc(valor) {
  return String(valor ?? "").replace(/[&<>"']/g, (c) =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

function formatarData(iso) {
  if (!iso) return "—";
  const [a, m, d] = iso.split("-");
  return `${d}/${m}/${a}`;
}

function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function nomeVeiculo(id) {
  const v = estado.veiculos.find((x) => x.id === id);
  return v ? `${v.marca} ${v.modelo} (${v.placa})` : `#${id}`;
}

let timerToast;
function avisar(mensagem, erro = false) {
  const t = $("toast");
  t.textContent = mensagem;
  t.className = "toast" + (erro ? " erro" : "");
  t.hidden = false;
  clearTimeout(timerToast);
  timerToast = setTimeout(() => (t.hidden = true), erro ? 6000 : 3000);
}

/* ---------- Comunicação com a API ---------- */

async function api(caminho, metodo = "GET", corpo = null) {
  const opcoes = { method: metodo, headers: {} };
  if (corpo) {
    opcoes.headers["Content-Type"] = "application/json";
    opcoes.body = JSON.stringify(corpo);
  }
  const resp = await fetch(caminho, opcoes);
  if (resp.status === 204) return null;

  let dados = null;
  try { dados = await resp.json(); } catch (e) { /* resposta sem corpo */ }

  if (!resp.ok) {
    let msg = (dados && dados.mensagem) || `Erro ${resp.status}`;
    if (dados && dados.campos) {
      msg += ": " + Object.values(dados.campos).join("; ");
    }
    throw new Error(msg);
  }
  return dados;
}

// Executa uma ação e mostra o erro (se houver) em um aviso
async function executar(acao, mensagemSucesso) {
  try {
    await acao();
    if (mensagemSucesso) avisar(mensagemSucesso);
  } catch (e) {
    avisar(e.message, true);
  }
}

/* ---------- Abas ---------- */

document.querySelectorAll(".aba").forEach((botao) => {
  botao.addEventListener("click", () => {
    document.querySelectorAll(".aba").forEach((b) => b.classList.remove("ativa"));
    botao.classList.add("ativa");
    ["veiculos", "manutencoes", "documentos"].forEach((nome) => {
      $("aba-" + nome).hidden = nome !== botao.dataset.aba;
    });
  });
});

/* ---------- Carregamento geral ---------- */

async function carregarTudo() {
  [estado.veiculos, estado.manutencoes, estado.documentos] = await Promise.all([
    api("/veiculos"),
    api("/manutencoes"),
    api("/documentos"),
  ]);
  preencherSelects();
  renderVeiculos();
  renderManutencoes();
  renderDocumentos();
  renderResumo();
}

function preencherSelects() {
  const opcoes = estado.veiculos
    .map((v) => `<option value="${v.id}">${esc(v.marca)} ${esc(v.modelo)} (${esc(v.placa)})</option>`)
    .join("");

  ["manutencaoVeiculo", "documentoVeiculo"].forEach((id) => {
    const select = $(id);
    const atual = select.value;
    select.innerHTML = opcoes || '<option value="">Cadastre um veículo primeiro</option>';
    if (atual) select.value = atual;
  });

  ["filtroManutencao", "filtroDocumento"].forEach((id) => {
    const select = $(id);
    const atual = select.value;
    select.innerHTML = '<option value="">Todos</option>' + opcoes;
    select.value = atual;
  });
}

/* ---------- Resumo ---------- */

function situacaoDocumento(doc) {
  if (!doc.dataVencimento) return { classe: "neutro", texto: "Sem vencimento", dias: null };
  const [a, m, d] = doc.dataVencimento.split("-").map(Number);
  const hoje = new Date(); hoje.setHours(0, 0, 0, 0);
  const dias = Math.round((new Date(a, m - 1, d) - hoje) / 86400000);
  if (dias < 0) return { classe: "vencido", texto: `Vencido há ${-dias} dia(s)`, dias };
  if (dias <= 30) return { classe: "aviso", texto: `Vence em ${dias} dia(s)`, dias };
  return { classe: "ok", texto: "Em dia", dias };
}

function renderResumo() {
  $("resVeiculos").textContent = estado.veiculos.length;
  $("resManutencoes").textContent = estado.manutencoes.length;
  $("resGasto").textContent = formatarMoeda(
    estado.manutencoes.reduce((soma, m) => soma + Number(m.custo || 0), 0));
  $("resAlertas").textContent = estado.documentos.filter((d) => {
    const s = situacaoDocumento(d);
    return s.classe === "vencido" || s.classe === "aviso";
  }).length;
}

/* ==========================================================
   VEÍCULOS
   ========================================================== */

function renderVeiculos() {
  const corpo = $("tbVeiculos");
  if (!estado.veiculos.length) {
    corpo.innerHTML = '<tr><td colspan="8" class="vazio">Nenhum veículo cadastrado.</td></tr>';
    return;
  }
  corpo.innerHTML = estado.veiculos.map((v) => `
    <tr>
      <td>${v.id}</td>
      <td>${esc(v.marca)} ${esc(v.modelo)}</td>
      <td>${v.ano}</td>
      <td>${esc(v.placa)}</td>
      <td>${esc(v.cor)}</td>
      <td>${Number(v.quilometragem).toLocaleString("pt-BR")}</td>
      <td>${esc(v.combustivel)}</td>
      <td class="acoes-tabela">
        <button class="btn pequeno" onclick="editarVeiculo(${v.id})">Editar</button>
        <button class="btn pequeno perigo" onclick="excluirVeiculo(${v.id})">Excluir</button>
      </td>
    </tr>`).join("");
}

function limparFormVeiculo() {
  $("formVeiculo").reset();
  $("veiculoId").value = "";
  $("tituloFormVeiculo").textContent = "Novo veículo";
  $("btnCancelarVeiculo").hidden = true;
}

$("btnCancelarVeiculo").addEventListener("click", limparFormVeiculo);

$("formVeiculo").addEventListener("submit", (e) => {
  e.preventDefault();
  const id = $("veiculoId").value;
  const dados = {
    marca: $("veiculoMarca").value.trim(),
    modelo: $("veiculoModelo").value.trim(),
    ano: Number($("veiculoAno").value),
    placa: $("veiculoPlaca").value.trim().toUpperCase(),
    cor: $("veiculoCor").value.trim(),
    quilometragem: Number($("veiculoKm").value),
    combustivel: $("veiculoCombustivel").value,
  };
  executar(async () => {
    if (id) await api(`/veiculos/${id}`, "PUT", dados);
    else await api("/veiculos", "POST", dados);
    limparFormVeiculo();
    await carregarTudo();
  }, id ? "Veículo atualizado!" : "Veículo cadastrado!");
});

window.editarVeiculo = (id) => {
  const v = estado.veiculos.find((x) => x.id === id);
  if (!v) return;
  $("veiculoId").value = v.id;
  $("veiculoMarca").value = v.marca;
  $("veiculoModelo").value = v.modelo;
  $("veiculoAno").value = v.ano;
  $("veiculoPlaca").value = v.placa;
  $("veiculoCor").value = v.cor;
  $("veiculoKm").value = v.quilometragem;
  $("veiculoCombustivel").value = v.combustivel;
  $("tituloFormVeiculo").textContent = `Editando veículo #${v.id}`;
  $("btnCancelarVeiculo").hidden = false;
  window.scrollTo({ top: 0, behavior: "smooth" });
};

window.excluirVeiculo = (id) => {
  if (!confirm("Excluir este veículo? Suas manutenções e documentos também serão apagados.")) return;
  executar(async () => {
    await api(`/veiculos/${id}`, "DELETE");
    await carregarTudo();
  }, "Veículo excluído!");
};

/* ==========================================================
   MANUTENÇÕES
   ========================================================== */

function renderManutencoes() {
  const filtro = $("filtroManutencao").value;
  const lista = estado.manutencoes.filter((m) => !filtro || String(m.veiculoId) === filtro);
  const corpo = $("tbManutencoes");

  if (!lista.length) {
    corpo.innerHTML = '<tr><td colspan="8" class="vazio">Nenhuma manutenção registrada.</td></tr>';
    $("totalManutencoes").innerHTML = "";
    return;
  }
  corpo.innerHTML = lista.map((m) => `
    <tr>
      <td>${formatarData(m.data)}</td>
      <td>${esc(nomeVeiculo(m.veiculoId))}</td>
      <td title="${esc(m.descricao)}">${esc(m.tipo)}</td>
      <td>${Number(m.quilometragem).toLocaleString("pt-BR")}</td>
      <td>${formatarMoeda(m.custo)}</td>
      <td>${esc(m.oficina) || "—"}</td>
      <td>${formatarData(m.proximaRevisao)}</td>
      <td class="acoes-tabela">
        <button class="btn pequeno" onclick="editarManutencao(${m.id})">Editar</button>
        <button class="btn pequeno perigo" onclick="excluirManutencao(${m.id})">Excluir</button>
      </td>
    </tr>`).join("");

  const total = lista.reduce((s, m) => s + Number(m.custo || 0), 0);
  $("totalManutencoes").innerHTML = `Total no filtro: <strong>${formatarMoeda(total)}</strong>`;
}

$("filtroManutencao").addEventListener("change", renderManutencoes);

function limparFormManutencao() {
  $("formManutencao").reset();
  $("manutencaoId").value = "";
  $("tituloFormManutencao").textContent = "Nova manutenção";
  $("btnCancelarManutencao").hidden = true;
}

$("btnCancelarManutencao").addEventListener("click", limparFormManutencao);

$("formManutencao").addEventListener("submit", (e) => {
  e.preventDefault();
  const id = $("manutencaoId").value;
  const dados = {
    veiculoId: Number($("manutencaoVeiculo").value),
    tipo: $("manutencaoTipo").value.trim(),
    descricao: $("manutencaoDescricao").value.trim(),
    data: $("manutencaoData").value,
    quilometragem: Number($("manutencaoKm").value),
    custo: Number($("manutencaoCusto").value),
    oficina: $("manutencaoOficina").value.trim(),
    proximaRevisao: $("manutencaoProxima").value || null,
  };
  executar(async () => {
    if (id) await api(`/manutencoes/${id}`, "PUT", dados);
    else await api("/manutencoes", "POST", dados);
    limparFormManutencao();
    await carregarTudo();
  }, id ? "Manutenção atualizada!" : "Manutenção registrada!");
});

window.editarManutencao = (id) => {
  const m = estado.manutencoes.find((x) => x.id === id);
  if (!m) return;
  $("manutencaoId").value = m.id;
  $("manutencaoVeiculo").value = m.veiculoId;
  $("manutencaoTipo").value = m.tipo;
  $("manutencaoDescricao").value = m.descricao || "";
  $("manutencaoData").value = m.data;
  $("manutencaoKm").value = m.quilometragem;
  $("manutencaoCusto").value = m.custo;
  $("manutencaoOficina").value = m.oficina || "";
  $("manutencaoProxima").value = m.proximaRevisao || "";
  $("tituloFormManutencao").textContent = `Editando manutenção #${m.id}`;
  $("btnCancelarManutencao").hidden = false;
  window.scrollTo({ top: 0, behavior: "smooth" });
};

window.excluirManutencao = (id) => {
  if (!confirm("Excluir esta manutenção?")) return;
  executar(async () => {
    await api(`/manutencoes/${id}`, "DELETE");
    await carregarTudo();
  }, "Manutenção excluída!");
};

/* ==========================================================
   DOCUMENTOS
   ========================================================== */

function renderDocumentos() {
  const filtro = $("filtroDocumento").value;
  const lista = estado.documentos.filter((d) => !filtro || String(d.veiculoId) === filtro);
  const corpo = $("tbDocumentos");

  if (!lista.length) {
    corpo.innerHTML = '<tr><td colspan="7" class="vazio">Nenhum documento cadastrado.</td></tr>';
    return;
  }
  corpo.innerHTML = lista.map((d) => {
    const s = situacaoDocumento(d);
    return `
    <tr>
      <td>${esc(nomeVeiculo(d.veiculoId))}</td>
      <td title="${esc(d.observacao)}">${esc(d.tipo)}</td>
      <td>${esc(d.numero)}</td>
      <td>${formatarData(d.dataEmissao)}</td>
      <td>${formatarData(d.dataVencimento)}</td>
      <td><span class="selo ${s.classe}">${s.texto}</span></td>
      <td class="acoes-tabela">
        <button class="btn pequeno" onclick="editarDocumento(${d.id})">Editar</button>
        <button class="btn pequeno perigo" onclick="excluirDocumento(${d.id})">Excluir</button>
      </td>
    </tr>`;
  }).join("");
}

$("filtroDocumento").addEventListener("change", renderDocumentos);

function limparFormDocumento() {
  $("formDocumento").reset();
  $("documentoId").value = "";
  $("tituloFormDocumento").textContent = "Novo documento";
  $("btnCancelarDocumento").hidden = true;
}

$("btnCancelarDocumento").addEventListener("click", limparFormDocumento);

$("formDocumento").addEventListener("submit", (e) => {
  e.preventDefault();
  const id = $("documentoId").value;
  const dados = {
    veiculoId: Number($("documentoVeiculo").value),
    tipo: $("documentoTipo").value,
    numero: $("documentoNumero").value.trim(),
    dataEmissao: $("documentoEmissao").value || null,
    dataVencimento: $("documentoVencimento").value || null,
    observacao: $("documentoObs").value.trim(),
  };
  executar(async () => {
    if (id) await api(`/documentos/${id}`, "PUT", dados);
    else await api("/documentos", "POST", dados);
    limparFormDocumento();
    await carregarTudo();
  }, id ? "Documento atualizado!" : "Documento cadastrado!");
});

window.editarDocumento = (id) => {
  const d = estado.documentos.find((x) => x.id === id);
  if (!d) return;
  $("documentoId").value = d.id;
  $("documentoVeiculo").value = d.veiculoId;
  $("documentoTipo").value = d.tipo;
  $("documentoNumero").value = d.numero;
  $("documentoEmissao").value = d.dataEmissao || "";
  $("documentoVencimento").value = d.dataVencimento || "";
  $("documentoObs").value = d.observacao || "";
  $("tituloFormDocumento").textContent = `Editando documento #${d.id}`;
  $("btnCancelarDocumento").hidden = false;
  window.scrollTo({ top: 0, behavior: "smooth" });
};

window.excluirDocumento = (id) => {
  if (!confirm("Excluir este documento?")) return;
  executar(async () => {
    await api(`/documentos/${id}`, "DELETE");
    await carregarTudo();
  }, "Documento excluído!");
};

/* ---------- Início ---------- */
executar(carregarTudo);
