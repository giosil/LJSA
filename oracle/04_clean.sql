--
-- Clean Schedulazioni
--

DELETE FROM LJSA_LOG_FILES;
DELETE FROM LJSA_LOG;

COMMIT;

DELETE FROM LJSA_SCHEDULAZIONI_PARAMETRI;
DELETE FROM LJSA_SCHEDULAZIONI_NOTIFICA;
DELETE FROM LJSA_SCHEDULAZIONI_CONF;
DELETE FROM LJSA_SCHEDULAZIONI;

COMMIT;

--
-- Clean Attivita
--

DELETE FROM LJSA_ATTIVITA_PARAMETRI;
DELETE FROM LJSA_ATTIVITA_NOTIFICA;
DELETE FROM LJSA_ATTIVITA_CONF;
DELETE FROM LJSA_ATTIVITA;

COMMIT;

DELETE FROM LJSA_LOG_SCHEDULATORE;

COMMIT;