# Change Log

## [6.0.10] - 2019-09-10

### Changed
- Generazione del file INSTALL.md (estratto dal README) per aggiunta delle sole istruzioni di configurazione nei pacchetti di installazione.

## [6.0.9] - 2019-02-25

### Changed
- Aggiornata la dipendenza del broker per supporto a numero di documenti superiore a 16 milioni. La versione di common è rimasta inalterata per quanto indicato come fix sulla versione 6.0.1 del progetto corrente)

## [6.0.8] - 2018-12-21

### Fixed
- Aggiornata versione di abstract-fca per fix su verifica porta socket del servizio FCA
- Corretta la versione e la data di rilascio dell'applicazione indicati all'avvio sui log

## [6.0.7] - 2018-11-16

### Fixed
- Corretto il formato dell'utente notificato ad eXtraWay per FCA (eliminato l'id del thread)

## [6.0.6] - 2018-09-25

### Changed
- Corretti errori presenti sul file README.md

## [6.0.5] - 2018-02-21

### Fixed
- Aggiunto path assoluto in filePattern di log4j2 per problemi di rollover

## [6.0.4] - 2018-02-08

### Fixed
- Corretta configurazione log4j2 per elminazione di vecchie copie dei file di log (passaggio da configurazione tramite properties a xml)

## [6.0.3] - 2017-10-25

### Changed
- Aggiunta la notifica dell'utente FCA in connessione con il server eXtraWay
- Saltata versione 6.0.2 per mantenimento dell'allineamento con versione di FCS

## [6.0.1] - 2017-09-22

### Changed
- Aggiornata la versione di common4.jar (per ripristino della libreria del configuratore - ver. 1.0.2)

### Fixed
- Corretto bug in caricamento dei parametri di attivazione (lettura sempre da singleton di configurazione)

## [6.0.0] - 2017-09-05

### Added
- Realizzazione dell'implementazione di abstract-fca specifica per DocWay4
- Possibilità di utilizzare questa versione di FCA sia con sono eXtraWay che con la versione eXtraWay/Elasticsearch
