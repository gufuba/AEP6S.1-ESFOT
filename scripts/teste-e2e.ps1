<#
.SYNOPSIS
  Teste de ponta a ponta da API EduConecta contra um MongoDB real.

.DESCRIPTION
  Percorre o fluxo completo (voluntarios, turmas, alunos com subdocumentos, matriculas,
  integridade referencial e erros) e confere o status HTTP de cada chamada.
  Requer curl.exe (nativo no Windows 10+). Termina com a quantidade de falhas.

  Use um banco descartavel para nao misturar com seus dados:

    1) java -jar target/educonecta-poc.jar --educonecta.mongodb.database=educonecta_e2e
    2) powershell -File scripts/teste-e2e.ps1
    3) mongosh --eval "db.getSiblingDB('educonecta_e2e').dropDatabase()"
#>
$base = "http://localhost:8080"
$script:falhas = 0
$script:total = 0

function Call($method, $path, $body = $null) {
    $curlArgs = @("-s", "-X", $method, "-w", "`n%{http_code}", "-H", "Content-Type: application/json")
    if ($body) {
        $tmp = [System.IO.Path]::GetTempFileName()
        [System.IO.File]::WriteAllText($tmp, $body, (New-Object System.Text.UTF8Encoding($false)))
        $curlArgs += @("--data-binary", "@$tmp")
    }
    $curlArgs += "$base$path"
    $text = (& curl.exe @curlArgs) -join "`n"
    $idx = $text.LastIndexOf("`n")
    $code = [int]$text.Substring($idx + 1)
    $bodyText = $text.Substring(0, $idx)
    $json = $null
    if ($bodyText.Trim().StartsWith("{") -or $bodyText.Trim().StartsWith("[")) { $json = $bodyText | ConvertFrom-Json }
    [pscustomobject]@{ Code = $code; Text = $bodyText; Json = $json }
}

function Expect($rotulo, $r, $esperado) {
    $script:total++
    $ok = ($r.Code -eq $esperado)
    if (-not $ok) { $script:falhas++ }
    $marca = if ($ok) { "OK  " } else { "FALHA" }
    $resumo = if ($r.Text.Length -gt 140) { $r.Text.Substring(0, 140) + "..." } else { $r.Text }
    Write-Host ("[{0}] {1} -> esperado {2}, obtido {3} {4}" -f $marca, $rotulo, $esperado, $r.Code, $resumo)
}

# --- Voluntarios
$v = Call POST "/api/voluntarios" '{"nome":"Carlos Pereira","email":"Carlos.Pereira@email.com","disciplinas":["Matematica","Fisica"]}'
Expect "cadastrar voluntario" $v 201
$vid = $v.Json.id
Expect "voluntario com e-mail duplicado" (Call POST "/api/voluntarios" '{"nome":"Outro","email":"carlos.pereira@email.com","disciplinas":["Historia"]}') 409
Expect "voluntario com e-mail invalido" (Call POST "/api/voluntarios" '{"nome":"X","email":"sem-arroba","disciplinas":["Historia"]}') 400
Expect "voluntario sem disciplinas" (Call POST "/api/voluntarios" '{"nome":"X","email":"x@email.com","disciplinas":[]}') 400
$v2 = Call POST "/api/voluntarios" '{"nome":"Helena Lima","email":"helena@email.com","disciplinas":["Portugues"]}'
Expect "cadastrar segundo voluntario" $v2 201

# --- Turmas
$t = Call POST "/api/turmas" ('{"nome":"Matematica - Tarde A","disciplina":"Matematica","turno":"Tarde","vagas":2,"voluntarioId":"' + $vid + '"}')
Expect "cadastrar turma" $t 201
$tid = $t.Json.id
Expect "turma de disciplina que o voluntario nao leciona" (Call POST "/api/turmas" ('{"nome":"Historia","disciplina":"Historia","turno":"Tarde","vagas":5,"voluntarioId":"' + $vid + '"}')) 409
Expect "turma com voluntario inexistente" (Call POST "/api/turmas" '{"nome":"T","disciplina":"Matematica","turno":"Tarde","vagas":5,"voluntarioId":"000000000000000000000000"}') 404
Expect "turma com id de voluntario invalido" (Call POST "/api/turmas" '{"nome":"T","disciplina":"Matematica","turno":"Tarde","vagas":5,"voluntarioId":"abc"}') 400
Expect "turma sem vagas informadas" (Call POST "/api/turmas" ('{"nome":"T","disciplina":"Matematica","turno":"Tarde","voluntarioId":"' + $vid + '"}')) 400

# --- Alunos (documentos aninhados)
$alunoMaria = '{"nome":"Maria Silva","idade":10,"curso":"Matematica","turno":"Tarde","endereco":{"rua":"Rua das Flores, 120","bairro":"Jardim Alvorada","cidade":"Maringa","estado":"pr"},"responsaveis":[{"nome":"Ana Souza","parentesco":"Mae","telefone":"(44) 99999-1234"},{"nome":"Jose Silva","parentesco":"Pai","telefone":"44 3322-1100"}]}'
$a1 = Call POST "/api/alunos" $alunoMaria
Expect "cadastrar aluno com endereco e 2 responsaveis" $a1 201
$a1id = $a1.Json.id
Write-Host ("      endereco.estado normalizado = " + $a1.Json.endereco.estado + " | telefone normalizado = " + $a1.Json.responsaveis[0].telefone)
Expect "menor de idade sem responsavel" (Call POST "/api/alunos" '{"nome":"Bebe","idade":9,"curso":"Matematica","turno":"Tarde"}') 400
Expect "aluno abaixo da idade minima" (Call POST "/api/alunos" '{"nome":"Bebe","idade":3,"curso":"Matematica","turno":"Tarde","responsaveis":[{"nome":"Ana","parentesco":"Mae","telefone":"44999991234"}]}') 400
Expect "responsavel com telefone invalido" (Call POST "/api/alunos" '{"nome":"X","idade":10,"curso":"Matematica","turno":"Tarde","responsaveis":[{"nome":"Ana","parentesco":"Mae","telefone":"123"}]}') 400
$a2 = Call POST "/api/alunos" '{"nome":"Joao Costa","idade":12,"curso":"Matematica","turno":"Tarde","responsaveis":[{"nome":"Rita","parentesco":"Avo","telefone":"44988887777"}]}'
Expect "cadastrar aluno 2" $a2 201
$a3 = Call POST "/api/alunos" '{"nome":"Pedro Alves","idade":20,"curso":"Matematica","turno":"Noite"}'
Expect "cadastrar aluno 3 (adulto, sem responsavel)" $a3 201
$a4 = Call POST "/api/alunos" '{"nome":"Ana Lima","idade":19,"curso":"Portugues","turno":"Tarde"}'
Expect "cadastrar aluno 4 (outra disciplina)" $a4 201

# --- Matriculas (relacionamento)
$m1 = Call POST "/api/turmas/$tid/alunos/$a1id"
Expect "matricular aluno 1" $m1 200
Write-Host ("      aluno 1 turmaId = " + $m1.Json.turmaId + " (turma = $tid)")
Expect "matricular aluno 2" (Call POST "/api/turmas/$tid/alunos/$($a2.Json.id)") 200
Expect "matricular aluno 1 de novo" (Call POST "/api/turmas/$tid/alunos/$a1id") 409
Expect "turma lotada (aluno 3)" (Call POST "/api/turmas/$tid/alunos/$($a3.Json.id)") 409
Expect "disciplina diferente (aluno 4)" (Call POST "/api/turmas/$tid/alunos/$($a4.Json.id)") 409
Expect "matricular em turma inexistente" (Call POST "/api/turmas/000000000000000000000000/alunos/$a1id") 404
Expect "matricular aluno inexistente" (Call POST "/api/turmas/$tid/alunos/000000000000000000000000") 404
$lista = Call GET "/api/turmas/$tid/alunos"
Expect "listar alunos da turma" $lista 200
Write-Host ("      alunos na turma = " + @($lista.Json).Count)

# --- Integridade referencial
Expect "remover turma com alunos" (Call DELETE "/api/turmas/$tid") 409
Expect "remover voluntario com turma" (Call DELETE "/api/voluntarios/$vid") 409
Expect "trocar disciplina de aluno matriculado" (Call PUT "/api/alunos/$a1id" ($alunoMaria -replace '"curso":"Matematica"', '"curso":"Portugues"')) 409
Expect "reduzir vagas abaixo dos matriculados" (Call PUT "/api/turmas/$tid" ('{"nome":"Matematica - Tarde A","disciplina":"Matematica","turno":"Tarde","vagas":1,"voluntarioId":"' + $vid + '"}')) 409
Expect "atualizar aluno mantendo turma" (Call PUT "/api/alunos/$a1id" ($alunoMaria -replace 'Maria Silva', 'Maria Silva Souza')) 200
$a1b = Call GET "/api/alunos/$a1id"
Write-Host ("      apos PUT: nome = " + $a1b.Json.nome + " | turmaId preservado = " + ($a1b.Json.turmaId -eq $tid))
Expect "PUT com idade abaixo do minimo (regressao)" (Call PUT "/api/alunos/$a1id" ($alunoMaria -replace '"idade":10', '"idade":3')) 400
Expect "menor sem responsavel via PUT" (Call PUT "/api/alunos/$a1id" '{"nome":"Maria","idade":10,"curso":"Matematica","turno":"Tarde"}') 400

# --- Desmatricular, reutilizar vaga e limpar
Expect "desmatricular aluno 2" (Call DELETE "/api/turmas/$tid/alunos/$($a2.Json.id)") 204
Expect "desmatricular aluno 2 de novo" (Call DELETE "/api/turmas/$tid/alunos/$($a2.Json.id)") 409
Expect "matricular aluno 3 na vaga liberada" (Call POST "/api/turmas/$tid/alunos/$($a3.Json.id)") 200
Expect "desmatricular aluno 1" (Call DELETE "/api/turmas/$tid/alunos/$a1id") 204
Expect "desmatricular aluno 3" (Call DELETE "/api/turmas/$tid/alunos/$($a3.Json.id)") 204
Expect "remover turma vazia" (Call DELETE "/api/turmas/$tid") 204
Expect "buscar turma removida" (Call GET "/api/turmas/$tid") 404
Expect "remover voluntario sem turmas" (Call DELETE "/api/voluntarios/$vid") 204

# --- Erros gerais
Expect "id mal formado" (Call GET "/api/alunos/id-invalido") 400
Expect "JSON mal formado" (Call POST "/api/alunos" '{nome:') 400
Expect "array no lugar de objeto" (Call POST "/api/alunos" '[{"nome":"X"}]') 400
Expect "tipo invalido (idade como texto)" (Call POST "/api/alunos" '{"nome":"X","idade":"abc","curso":"M","turno":"T"}') 400

# --- OpenAPI
$api = Call GET "/v3/api-docs"
Expect "documentacao OpenAPI" $api 200
Write-Host ("      titulo = " + $api.Json.info.title + " v" + $api.Json.info.version + " | tags = " + (($api.Json.tags | ForEach-Object { $_.name }) -join ", ") + " | paths = " + @($api.Json.paths.PSObject.Properties).Count)

Write-Host ""
Write-Host ("RESULTADO: {0} verificacoes, {1} falhas" -f $script:total, $script:falhas)
