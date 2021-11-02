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
        static sID_SERVIZIO = 'id_servizio';
        static sDESCRIZIONE = 'descrizione';
        static sATTIVO      = 'attivo';
        static sCREDENZIALI = 'credenziali';
    }

    export class IClasse {
        static sCLASSE      = "classe";
        static sDESCRIZIONE = "descrizione";
        static sATTIVITA    = "attivita";
    }

    export class ICredenziale {
        static sID_SERVIZIO    = "id_servizio";
        static sID_CREDENZIALE = "id_credenziale";
        static sCREDENZIALI    = "credenziali";
        static sEMAIL          = "email";
        static sATTIVO         = "attivo";
    }
}

WUX.global.locale = GUI.getLocale();

var jrpc = new JRPC("/LJSA/rpc");

jrpc.setUserName(GUI.getUserLogged().userName);
jrpc.setPassword(GUI.getUserLogged().tokenId);

