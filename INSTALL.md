# FCA-DocWay - INSTALLAZIONE


## Prerequisiti

1. _Java8_


## Configurazione

Prima di poter avviare l'applicazione DocWay-FCA occorre configurare i seguenti files:

- _it.tredi.abstract-fca.properties_ (directory _classes_)
- _it.tredi.docway-fca.properties_ (directory _classes_)
- _it.highwaytech.broker.properties_ (directory _classes_)
- _log4j2.xml_ (directory _classes_)
- _docway-fca_ se ambiente linux, _docway-fca.bat_ (per avvio manuale) se ambiente windows, _install_32.bat_ _install_amd64.bat_ _install_ia64.bat_ (per istallazione come servizio a seconda del sistema) se ambiente windows (directory _bin_)

Per maggiori informazioni sulla configurazione si rimanda ai commenti presenti nei file di properties.

__N.B.__: Oltre alla configurazione applicativa occorre disattivare il popolamento automatico della directory '_conversion_' su eXtraWay, utilizzata dalla precedente release di FCA. Per fare questo, dalla versione __1.4.10__ di eXtraWay, è stata introdotta una specifica configurazione (per maggiori informazioni si rimanda alla documentazione di eXtraWay). Per le versioni precedenti di eXtraWay, occorre utilizzare una delle seguenti "soluzioni alternative":
- Fare puntare la directory '_conversion_' di eXtraWay a quella dei file temporanei del sistema operativo (in modo da farla svuotare in automatico)
- Generare uno script che si occupi di svuotare la directory ogni X minuti


### it.tredi.abstract-fca.properties

- Configurazione di tutti gli host FCS e del numero di thread che questi devono poter gestire
- Eventuale attivazione/disattivazione di estrazione testo, ocr o conversioni
- Eventuali limitazioni sulla dimensione o tipologia di files da gestire in estrazione del testo o conversione
- Gestione dei timeout del servizio (tempi di sleep)

### it.tredi.docway-fca.properties

- _xway.dbname_: Nome dell'archivio DocWay da indicizzare e/o sul quale eseguire le conversioni
- _xway.query_: Query che identifica i documenti da elaborare
- _xway.sort_: Ordinamento dei risultati della ricerca (ordine decrescente in modo da trattare con maggiore priorità gli ultimi documenti inseriti)
- _xway.query.max.docs_: Numero di documenti da caricare tramite la query impostata (pagina di titoli)

### it.highwaytech.broker.properties

- Configurazione di host, porta e numero connessioni verso eXtraWay
- Eventuale abilitazione dell'integrazione con Elasticsearch (__N.B.__: In caso di abilitazione di Elasticsearch occorre configurare anche il file cache.ccf con i parametri di attivazione di Apache JCF)

### log4j2.xml

- Percorso assoluto al file di logs di FCA (_property.filename_)
- Numero di file di log e dimenzione

### docway-fca (LINUX)

- File di avvio di DocWay FCA su Linux. Di seguito è riportata la sezione del file che deve __obbligatoriamente__ essere configurata:

```
# N.B.: E' richiesto Java8 per l'esecuzione del processo
JAVA_COMMAND=/usr/bin/java

# Massima dimensione della memoria heap per il FCA
MAXHEAP=-Xmx128m

FCAPID=/tmp/docway-fca.pid

NICE_LEVEL=10
```

### docway-fca.bat (WINDOWS)

- File per un avvio da riga di comando in ambiente Windows. Di seguito è riportata la sezione del file che deve __obbligatoriamente__ essere configurata:

```
rem la variabile JVM serve per impostare la java virtual machine che verra' utilizzata per avviare il servizio
rem per avviare da un persorso specifico si puo' settare la variabile come nell'esempio seguente
rem set "JVM=C:\JDKS\64bit\1.8.0_40\bin\java.exe"
rem NOTA BENE e' richiesta una versione di JVM non inferiore alla 1.8.0
set "JVM=java"

rem per settare le opzioni della JVM settare la variabile JVM_OPTS
set JVM_OPTS=-Xmx1024m -Xms1024m
```

### install-32.bat (WINDOWS)

- File per installare come servizio in ambiente Windows a 32 bit. Di seguito è riportata la sezione del file che deve __obbligatoriamente__ essere configurata:

```
set JVM=auto
rem settata con auto ricava la jvm dal registro di windows
rem per impostarla a una jvm specifica occorre settarla al path della jvm.dll
rem set JVM="C:\JDKS\32bit\1.8.0_40\jre\bin\server\jvm.dll"

set xms=1024m
set xmx=1024m
```

### install-amd64.bat (WINDOWS)

- File per installare come servizio in ambiente Windows amd a 64 bit. Di seguito è riportata la sezione del file che deve __obbligatoriamente__ essere configurata:

```
set JVM=auto
rem impostare a una jvm specifica occorre settarla al path della jvm.dll
rem set JVM="C:\JDKS\32bit\1.8.0_40\jre\bin\server\jvm.dll"

set xms=1024m
set xmx=1024m
```

### install-ia64.bat (WINDOWS)

- File per installare come servizio in ambiente Windows ia a 64 bit. Di seguito è riportata la sezione del file che deve __obbligatoriamente__ essere configurata:

```
set JVM=auto
rem impostare a una jvm specifica occorre settarla al path della jvm.dll
rem set JVM="C:\JDKS\32bit\1.8.0_40\jre\bin\server\jvm.dll"

set xms=1024m
set xmx=1024m
```

### uninstall.bat (WINDOWS)

- File per rimuovere il servizio in ambiente Windows.


## Esecuzione

__Linux__

```
bin/docway-fca {start|stop|status|restart|debug}
```

__Windows - Avvio Manuale__

Spostarsi da prompt nella directory bin e lanciare

```
docway-fca.bat
```

__Windows - Installazione Servizio__

Spostarsi da prompt nella directory bin e lanciare a seconda del sistema

```
install_32.bat
```
oppure

```
install_amd64.bat
```
oppure

```
install_ia64.bat
```

__Windows - Disinstallazione Servizio__

Spostarsi da prompt nella directory bin e lanciare a seconda del sistema

```
uninstall.bat
```
