================================================================================
DEISI World Meter - Projecto AED 2025/26 - Parte 2
================================================================================

Autores:
- a22404033 - Miguel Lopes
- a22408702 - Afonso Rodrigues


================================================================================
VIDEO DEMONSTRATIVO
================================================================================

Link YouTube: https://youtu.be/CHH3GtuW_TQ


================================================================================
COMANDO CRIATIVO
================================================================================

Nome: GET_COUNTRIES_LOSING_POPULATION <ano-inicio> <ano-fim>

Descricao:
Lista os paises que perderam populacao entre o ano-inicio e o ano-fim,
ordenados pela maior perda (decrescente) e, em caso de empate, por ordem
alfabetica do nome do pais. Apenas paises validos (com cidades) e com
registos de populacao em ambos os anos sao considerados. Se o ano-inicio
for maior ou igual ao ano-fim, devolve resultado vazio.

A perda e calculada como (populacao_total_ano_inicio - populacao_total_ano_fim)
onde populacao_total = populacao_masculina + populacao_feminina.

Formato de output: <nome_pais>:<perda>

Exemplo:

GET_COUNTRIES_LOSING_POPULATION 2020 2024
Ucrania:5971846
Japao:2613328
Russia:1660250
Italia:802835
Libano:443880
Bulgaria:360558
Servia:260978
Grecia:209511
Bielorrussia:178703
Lituania:127469
... (e mais paises)


Utilidade: revela tendencias demograficas de declinio populacional,
util para identificar paises afetados por guerra, emigracao em massa,
envelhecimento populacional ou outras crises demograficas.


================================================================================
UTILIZACAO DE IA GENERATIVA
================================================================================

Ferramenta/Modelo: Claude Sonnet 4.6 (Anthropic)

Prompt:
"Estou a implementar um comando que encontra pares de cidades em paises
diferentes a uma certa distancia (em km), usando a formula de haversine.
O problema e que para datasets com 50000+ cidades o algoritmo O(N*M) e
demasiado lento (timeout > 20s). Como posso optimizar a procura sem
recorrer a estruturas de dados complexas (KD-trees, etc.)?"

Resposta (resumida):
"Uma optimizacao simples e eficaz e usar um filtro por 'bounding box'
antes da chamada do haversine. Como 1 grau de latitude corresponde
aproximadamente a 111 km, podes calcular o delta maximo de latitude
e longitude para a distancia desejada:

    double maxLatDelta = distancia / 111.0 + margem;
    double maxLonDelta = distancia / (111.0 * cos(toRadians(lat))) + margem;

Para cada par de cidades, primeiro verificas se |lat1 - lat2| > maxLatDelta
ou |lon1 - lon2| > maxLonDelta - se sim, podes saltar a cidade sem ter de
calcular o haversine (que envolve sin, cos, sqrt e e bastante caro). Isto
permite uma reducao significativa do numero de calculos completos de
distancia, sem alterar a complexidade assimptotica mas com um speedup
constante grande na pratica."

Comentario sobre como/onde usamos:
Aplicamos esta optimizacao no metodo getCitiesAtDistance2 em Main.java.
Antes da optimizacao, o teste TestTeacherP2LargeFiles.getCitiesAtDistance2
fazia timeout (>20s). Apos aplicar o filtro de bounding box (skip por
diferenca de latitude/longitude antes do haversine), o teste passou a
executar em poucos segundos. A nossa formula final usa um maxLatDelta
calculado com base em 111 km/grau e um maxLonDelta ajustado pela latitude
da cidade-base (cos(latitude)), com uma pequena margem para evitar
floating-point edge cases.



