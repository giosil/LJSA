namespace GUI {

    export let LJSA_SERVICE = 'LJSA';

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

    export class IServizio {
        static sID_SERVIZIO = 'id_servizio';
        static sDESCRIZIONE = 'descrizione';
        static sATTIVO      = 'attivo';
        static sCREDENZIALI = 'credenziali';
    }

}

WUX.global.locale = GUI.getLocale();

var jrpc = new JRPC("/LJSA/rpc");

jrpc.setUserName(GUI.getUserLogged().userName);
jrpc.setPassword(GUI.getUserLogged().tokenId);

