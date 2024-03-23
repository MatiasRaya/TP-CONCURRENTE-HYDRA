import re

# Se crea una lista vacia
list = []

# Se abre el archivo log.txt en modo solo lectura
with open("../TPFinal/log.txt", "r") as file:
    # Se recorre el archivo linea por linea
    for line in file:
        # Se agrega la linea a la lista
        list.append(line)

# Se recorre la lista
for i in range(len(list)):
    # Se busca el patron T+numero
    list[i] = re.search(r"(.*?)(T\d{2}|T\d{1})",list[i]).group(2) 

# Se crea una cadena vacia
line = ""

# Se recorre la lista
for item in list:
    # Se agrega el item a la cadena
    line += item

# Se cuentan el numero de ocurrencias de T12, T6 y T7
invFiguras = line.count("T12")       #(T9T10T11T12)
invBlRed = line.count("T6")          #(T1T2T4T6T8)
invBlCua = line.count("T7")          #(T1T3T5T7T8)

# Se calcula el numero total de piezas completadas
invTotal = invFiguras + invBlCua + invBlRed

# Se reemplaza los caracteres "T10", "T11" y "T12" por "TA", "TB" y "TC" respectivamente en la cadena 'line'
line = line.replace("T10","TA")
line = line.replace("T11","TB")
line = line.replace("T12","TC")

# Se define un patrón de expresión regular y una sustitución para realizar un reemplazo específico en la cadena 'line'
regex = '(T1)(.*?)((T2)(.*?)(T4)(.*?)(T6)|(T3)(.*?)(T5)(.*?)(T7))(.*?)(T8)|(T9)(.*?)(TA)(.*?)(TB)(.*?)(TC)'
sub = '\g<2>\g<5>\g<7>\g<10>\g<12>\g<14>\g<17>\g<19>\g<21>'

# Se declara una variable booleana para verificar si el reemplazo fue exitoso
works = True

# Se realiza el reemplazo en la cadena 'line' hasta que no se puedan realizar más reemplazos
while(len(line) > 0):
    # Se almacena la cadena 'line' en una variable temporal
    line_temp = line

    # Se realiza el reemplazo en la cadena 'line' y se almacena el número de reemplazos realizados en la variable 'i'
    line, i = re.subn(regex, sub, line)

    # Si no se realizaron reemplazos, se detiene el bucle y se indica un fallo
    if(line == line_temp):
        print("Failed:", line)
        works = False
        break

# Se verifica si el reemplazo fue exitoso
if(works):
    # Se imprimen los resultados
    print("Numero de figuras de madera completados ([8, 9, 10, 11]):",invFiguras)
    print("Numero de bloques de madera cuadrados completadas ([0, 1, 3, 5, 7]):",invBlRed)
    print("Numero de bloques de madera redondos completadas ([0, 2, 4, 6, 7]):",invBlCua)
    print("Numero total de piezas completadas:",invTotal)
