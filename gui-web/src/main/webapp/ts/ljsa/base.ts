namespace GUI {

    import WUtil = WUX.WUtil;

    export let LJSA_SERVICE = 'LJSA';

    export let _defService = '';

    export interface User {
        id: number;
        userName: string;

        currLogin: Date;
        tokenId: string;

        lastName?: string;
        firstName?: string;
        email?: string;
        mobile?: string;
        role?: string;
        reference?: string;
        locale?: string;

        groups?: string[];
        structures?: string[];
    }

    /**
     * See below
     */
    export function getUserLogged(): User {
        let userLogged = window ? window['_userLogged'] : undefined;
        if (userLogged && typeof userLogged == 'object') return userLogged as User;
        return { id: 1, userName: 'dew', currLogin: new Date(), role: 'admin', groups: [LJSA_SERVICE], structures:[], email: 'test@dew.org', mobile: '3491234567', tokenId: 'KURJPghMTJ'};
    }

    export function getConfig(): any {
        let config = window ? window['_config'] : undefined;
        if (config && typeof config == 'object') return config;
        return {};
    }

    export function getLocale(): string {
        let u = getUserLogged();
        if(u != null && u.locale) return u.locale;
        return WUX.WUtil.getString(getConfig(), 'locale', '');
    }

    export function isDevMode(): boolean {
        let userLogged = window ? window['_userLogged'] : undefined;
        if (userLogged && typeof userLogged == 'object') return false;
        return true;
    }

    export function indexOf(a: any, k1: any, k2: any, v: any): number {
        if (!a || !k1 || !k2) return -1;
        let y = WUtil.toArray(a);
        for (let i = 0; i < y.length; i++) {
            let w = WUtil.getValue(y[i], k1) + ':' + WUtil.getValue(y[i], k2);
            if (w == v) return i;
        }
        return -1;
    }

    export class IServizio {
        static sID_SERVIZIO    = 'id_servizio';
        static sDESCRIZIONE    = 'descrizione';
        static sATTIVO         = 'attivo';
        static sCREDENZIALI    = 'credenziali';
    }

    export class IClasse {
        static sCLASSE         = "classe";
        static sDESCRIZIONE    = "descrizione";
        static sATTIVITA       = "attivita";
    }

    export class ICredenziale {
        static sID_SERVIZIO    = "id_servizio";
        static sID_CREDENZIALE = "id_credenziale";
        static sCREDENZIALI    = "credenziali";
        static sEMAIL          = "email";
        static sATTIVO         = "attivo";
    }

    export class IAttivita {
        static sID_SERVIZIO        = "id_servizio";
        static sID_ATTIVITA        = "id_attivita";
        static sDESCRIZIONE        = "descrizione";
        static sCLASSE             = "classe";
        static sATTIVO             = "attivo";

        static sID_CREDENZIALE_INS = "id_credenziale_ins";
        static sDATA_INS           = "data_inserimento";
        static sORA_INS            = "ora_inserimento";
        static sID_CREDENZIALE_AGG = "id_credenziale_agg";
        static sDATA_AGG           = "data_aggiornamento";
        static sORA_AGG            = "ora_aggiornamento";

        static sCONFIGURAZIONE     = "configurazione";
        static sCONF_OPZIONE       = "opzione";
        static sCONF_DESCRIZIONE   = "descrizione";
        static sCONF_VALORI        = "valori";
        static sCONF_PREDEFINITO   = "predefinito";

        static sPARAMETRI          = "parametri";
        static sPAR_PARAMETRO      = "parametro";
        static sPAR_DESCRIZIONE    = "descrizione";
        static sPAR_VALORI         = "valori";
        static sPAR_PREDEFINITO    = "predefinito";

        static sNOTIFICA           = "notifica";
        static sNOT_EVENTO         = "evento";
        static sNOT_DESTINAZIONE   = "destinazione";
    }

    export class ISched {
        static sID_SCHEDULAZIONE      = "id_schedulazione";
        static sID_SERVIZIO           = "id_servizio";
        static sID_ATTIVITA           = "id_attivita";
        static sSCHEDULAZIONE         = "schedulazione";
        static sDESCRIZIONE           = "descrizione";
        static sID_CREDENZIALE_INS    = "id_credenziale_ins";
        static sDATA_INS              = "data_inserimento";
        static sORA_INS               = "ora_inserimento";
        static sID_CREDENZIALE_AGG    = "id_credenziale_agg";
        static sDATA_AGG              = "data_aggiornamento";
        static sORA_AGG               = "ora_aggiornamento";
        static sSTATO                 = "stato";
        static sINIZIO_VALIDITA       = "inizio_validita";
        static sFINE_VALIDITA         = "fine_validita";
        static sDATA_SCHED            = "data_schedulazione";
        static sORA_SCHED             = "ora_schedulazione";
        static sATTIVO                = "a";

        static sPARAMETRI             = "parametri";
        static sPAR_PARAMETRO         = "parametro";
        static sPAR_VALORE            = "valore";
        static sPAR_DA_ATTIVITA       = "da_attivita";
        static sPAR_OVERWRITE         = "overwrite";
        static sPAR_DESCRIZIONE       = IAttivita.sPAR_DESCRIZIONE;
        static sPAR_VALORI            = IAttivita.sPAR_VALORI;
        static sPAR_PREDEFINITO       = IAttivita.sPAR_PREDEFINITO;

        static sCONFIGURAZIONE        = "configurazione";
        static sCONF_OPZIONE          = "opzione";
        static sCONF_VALORE           = "valore";
        static sCONF_DA_ATTIVITA      = "da_attivita";
        static sCONF_OVERWRITE        = "overwrite";
        static sCONF_DESCRIZIONE      = IAttivita.sCONF_DESCRIZIONE;
        static sCONF_VALORI           = IAttivita.sCONF_VALORI;
        static sCONF_PREDEFINITO      = IAttivita.sCONF_PREDEFINITO;
        static sCONF_TIMEOUT          = "timeout";
        static sCONF_STOP_ON_TIMEOUT  = "stopOnTimeout";
        static sCONF_COMPRESS_FILES   = "compressFiles";
        static sCONF_ATTACH_FILES     = "attachFiles";
        static sCONF_ATTACH_ERR_FILES = "attachErrorFiles";
        static sCONF_FILE_INFO        = "fileInfo";
        static sCONF_NO_LOG           = "nolog";

        static sNOTIFICA              = "notifica";
        static sNOT_EVENTO            = "evento";
        static sNOT_DESTINAZIONE      = "destinazione";
        static sNOT_DA_ATTIVITA       = "da_attivita";
        static sNOT_CANCELLATA        = "cancellata";

        static sESEC_COMPLETATE       = "esecuzioni_completate";
        static sESEC_INTERROTTE       = "esecuzioni_interrotte";
    }
}

WUX.global.locale = GUI.getLocale();

var jrpc = new JRPC("/LJSA/rpc");

jrpc.setUserName(GUI.getUserLogged().userName);
jrpc.setPassword(GUI.getUserLogged().tokenId);

