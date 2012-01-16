CC = latex
PDFCC = dvipdf

DEP = userguide.tex
DEPDVI = userguide.dvi

all: dvi pdf 

dvi: ${DEP}
	${CC} ${DEP}
	${CC} ${DEP}
	${CC} ${DEP}

pdf: ${DEPDVI}
	${PDFCC} ${DEPDVI}

clean:
	rm -f *.aux *.dvi *.log *.pdf