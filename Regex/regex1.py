import re

list = []

with open("../TPFinal/log.txt", "r") as file:
    for line in file:
        list.append(line)

for i in range(len(list)):
    list[i] = re.search(r"(.*?)(T\d{2}|T\d{1})",list[i]).group(2) 

line = ""
for item in list:
    line+=item

invFiguras = line.count("T12")       #(T9T10T11T12)
invBlRed = line.count("T6")          #(T1T2T4T6T8)
invBlCua = line.count("T7")          #(T1T3T5T7T8)
invTotal = invFiguras + invBlCua + invBlRed
line = line.replace("T10","TA")
line = line.replace("T11","TB")
line = line.replace("T12","TC")


regex = '(T1)(.*?)((T2)(.*?)(T4)(.*?)(T6)|(T3)(.*?)(T5)(.*?)(T7))(.*?)(T8)|(T9)(.*?)(TA)(.*?)(TB)(.*?)(TC)'
sub = '\g<2>\g<5>\g<7>\g<10>\g<12>\g<14>\g<17>\g<19>\g<21>'

works = True
while(len(line)>0):
    line_temp = line
    line, i = re.subn(regex, sub, line)
    if(line==line_temp):
        print("Failed:", line)
        works = False
        break
    print(line, "\n")

if(works):
    print("Numero de figuras de madera completados:",invFiguras,"\n")
    print("Numero de bloques de madera cuadrados completadas:",invBlRed,"\n")
    print("Numero de bloques de madera redondos completadas:",invBlCua,"\n")
    print("Numero total de piezas completadas:",invTotal,"\n")
