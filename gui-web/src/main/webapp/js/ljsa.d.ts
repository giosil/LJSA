declare namespace GUI {
    class GUIAttivita extends WUX.WComponent {
        protected container: WUX.WContainer;
        protected tagsFilter: WUX.WTags;
        protected fpFilter: WUX.WFormPanel;
        protected btnFind: WUX.WButton;
        protected btnReset: WUX.WButton;
        protected btnNew: WUX.WButton;
        protected cntActions: AppTableActions;
        protected btnOpen: WUX.WButton;
        protected btnSave: WUX.WButton;
        protected btnCancel: WUX.WButton;
        protected btnDelete: WUX.WButton;
        protected tabResult: WUX.WDXTable;
        protected selId: any;
        protected tcoDetail: WUX.WTab;
        protected fpDetail: WUX.WFormPanel;
        protected selSerDet: LJSASelServizi;
        protected tabCon: WUX.WDXTable;
        protected btnAddCon: WUX.WButton;
        protected btnRemCon: WUX.WButton;
        protected tabPar: WUX.WDXTable;
        protected btnAddPar: WUX.WButton;
        protected btnRemPar: WUX.WButton;
        protected tabNot: WUX.WDXTable;
        protected btnAddNot: WUX.WButton;
        protected btnRemNot: WUX.WButton;
        protected dlgCon: DlgAttCon;
        protected dlgPar: DlgAttPar;
        protected dlgNot: DlgAttNot;
        protected isNew: boolean;
        protected status: number;
        readonly iSTATUS_STARTUP = 0;
        readonly iSTATUS_VIEW = 1;
        readonly iSTATUS_EDITING = 2;
        constructor(id?: string);
        protected render(): WUX.WContainer;
        collapseHandler(e: JQueryEventObject): void;
        protected onSelect(): void;
        protected clearDet(): void;
        protected enableDet(e: boolean): void;
    }
}
declare namespace GUI {
    let LJSA_SERVICE: string;
    let _defService: string;
    interface User {
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
    function getUserLogged(): User;
    function getConfig(): any;
    function getLocale(): string;
    function isDevMode(): boolean;
    function indexOf(a: any, k1: any, k2: any, v: any): number;
    function putUserLog(a: any): any;
    function getIdVal(a: any, k: any): any;
    class ICommon {
        static sUSER_LOG: string;
    }
    class IServizio {
        static sID_SERVIZIO: string;
        static sDESCRIZIONE: string;
        static sATTIVO: string;
        static sCREDENZIALI: string;
    }
    class IClasse {
        static sCLASSE: string;
        static sDESCRIZIONE: string;
        static sATTIVITA: string;
    }
    class ICredenziale {
        static sID_SERVIZIO: string;
        static sID_CREDENZIALE: string;
        static sCREDENZIALI: string;
        static sEMAIL: string;
        static sATTIVO: string;
    }
    class IAtt {
        static sID_SERVIZIO: string;
        static sID_ATTIVITA: string;
        static sDESCRIZIONE: string;
        static sCLASSE: string;
        static sATTIVO: string;
        static sID_CREDENZIALE_INS: string;
        static sDATA_INS: string;
        static sORA_INS: string;
        static sID_CREDENZIALE_AGG: string;
        static sDATA_AGG: string;
        static sORA_AGG: string;
        static sCONFIGURAZIONE: string;
        static sCONF_OPZIONE: string;
        static sCONF_DESCRIZIONE: string;
        static sCONF_VALORI: string;
        static sCONF_PREDEFINITO: string;
        static sPARAMETRI: string;
        static sPAR_PARAMETRO: string;
        static sPAR_DESCRIZIONE: string;
        static sPAR_VALORI: string;
        static sPAR_PREDEFINITO: string;
        static sNOTIFICA: string;
        static sNOT_EVENTO: string;
        static sNOT_DESTINAZIONE: string;
    }
    class ISched {
        static sID: string;
        static sID_SERVIZIO: string;
        static sID_ATTIVITA: string;
        static sSCHEDULAZIONE: string;
        static sDESCRIZIONE: string;
        static sID_CREDENZIALE_INS: string;
        static sDATA_INS: string;
        static sORA_INS: string;
        static sID_CREDENZIALE_AGG: string;
        static sDATA_AGG: string;
        static sORA_AGG: string;
        static sSTATO: string;
        static sINIZIO_VALIDITA: string;
        static sFINE_VALIDITA: string;
        static sDATA_SCHED: string;
        static sORA_SCHED: string;
        static sATTIVO: string;
        static sPARAMETRI: string;
        static sPAR_PARAMETRO: string;
        static sPAR_VALORE: string;
        static sPAR_DA_ATTIVITA: string;
        static sPAR_OVERWRITE: string;
        static sPAR_DESCRIZIONE: string;
        static sPAR_VALORI: string;
        static sPAR_PREDEFINITO: string;
        static sCONFIGURAZIONE: string;
        static sCONF_OPZIONE: string;
        static sCONF_VALORE: string;
        static sCONF_DA_ATTIVITA: string;
        static sCONF_OVERWRITE: string;
        static sCONF_DESCRIZIONE: string;
        static sCONF_VALORI: string;
        static sCONF_PREDEFINITO: string;
        static sCONF_TIMEOUT: string;
        static sCONF_STOP_ON_TIMEOUT: string;
        static sCONF_COMPRESS_FILES: string;
        static sCONF_ATTACH_FILES: string;
        static sCONF_ATTACH_ERR_FILES: string;
        static sCONF_FILE_INFO: string;
        static sCONF_NO_LOG: string;
        static sNOTIFICA: string;
        static sNOT_EVENTO: string;
        static sNOT_DESTINAZIONE: string;
        static sNOT_DA_ATTIVITA: string;
        static sNOT_CANCELLATA: string;
        static sESEC_COMPLETATE: string;
        static sESEC_INTERROTTE: string;
    }
}
declare var jrpc: JRPC;
declare namespace GUI {
    class GUIClassi extends WUX.WComponent {
        protected container: WUX.WContainer;
        protected tagsFilter: WUX.WTags;
        protected fpFilter: WUX.WFormPanel;
        protected btnFind: WUX.WButton;
        protected btnReset: WUX.WButton;
        protected btnNew: WUX.WButton;
        protected cntActions: AppTableActions;
        protected btnOpen: WUX.WButton;
        protected btnSave: WUX.WButton;
        protected btnCancel: WUX.WButton;
        protected btnDelete: WUX.WButton;
        protected tabResult: WUX.WDXTable;
        protected selId: any;
        protected tcoDetail: WUX.WTab;
        protected fpDetail: WUX.WFormPanel;
        protected isNew: boolean;
        protected status: number;
        readonly iSTATUS_STARTUP = 0;
        readonly iSTATUS_VIEW = 1;
        readonly iSTATUS_EDITING = 2;
        constructor(id?: string);
        protected render(): WUX.WContainer;
        collapseHandler(e: JQueryEventObject): void;
        protected onSelect(): void;
    }
}
declare namespace GUI {
    class AppTableActions extends WUX.WComponent {
        left: WUX.WContainer;
        right: WUX.WContainer;
        constructor(id: string);
        protected componentDidMount(): void;
        setLeftVisible(v: boolean): void;
        setRightVisible(v: boolean): void;
    }
    class LJSASelServizi extends WUX.WSelect2 {
        constructor(id?: string, multiple?: boolean);
        protected updateState(nextState: any): void;
        protected componentDidMount(): void;
    }
    class LJSASelClassi extends WUX.WSelect2 {
        constructor(id?: string, multiple?: boolean);
        protected componentDidMount(): void;
    }
    class LJSASelAttivita extends WUX.WSelect2 {
        protected _serviceL: string;
        protected _serviceS: string;
        constructor(id?: string, multiple?: boolean);
        set service(s: string);
        protected updateState(nextState: any): void;
        protected componentDidMount(): void;
    }
    class LJSASelStati extends WUX.WSelect2 {
        constructor(id?: string, multiple?: boolean);
    }
    class LJSASelEventi extends WUX.WSelect2 {
        constructor(id?: string, multiple?: boolean);
    }
}
declare namespace GUI {
    class GUICredenziali extends WUX.WComponent {
        protected container: WUX.WContainer;
        protected tagsFilter: WUX.WTags;
        protected fpFilter: WUX.WFormPanel;
        protected btnFind: WUX.WButton;
        protected btnReset: WUX.WButton;
        protected btnNew: WUX.WButton;
        protected cntActions: AppTableActions;
        protected btnOpen: WUX.WButton;
        protected btnSave: WUX.WButton;
        protected btnCancel: WUX.WButton;
        protected btnDelete: WUX.WButton;
        protected tabResult: WUX.WDXTable;
        protected selId: any;
        protected tcoDetail: WUX.WTab;
        protected fpDetail: WUX.WFormPanel;
        protected selSerDet: LJSASelServizi;
        protected isNew: boolean;
        protected status: number;
        readonly iSTATUS_STARTUP = 0;
        readonly iSTATUS_VIEW = 1;
        readonly iSTATUS_EDITING = 2;
        constructor(id?: string);
        protected render(): WUX.WContainer;
        collapseHandler(e: JQueryEventObject): void;
        protected onSelect(): void;
    }
}
declare namespace GUI {
    class DlgAttCon extends WUX.WDialog {
        protected fp: WUX.WFormPanel;
        constructor(id: string);
        protected updateState(nextState: any): void;
        getState(): object;
        protected onClickOk(): boolean;
    }
    class DlgAttPar extends WUX.WDialog {
        protected fp: WUX.WFormPanel;
        constructor(id: string);
        protected updateState(nextState: any): void;
        getState(): object;
        protected onClickOk(): boolean;
    }
    class DlgAttNot extends WUX.WDialog {
        protected fp: WUX.WFormPanel;
        constructor(id: string);
        protected updateState(nextState: any): void;
        getState(): object;
        protected onClickOk(): boolean;
    }
    class DlgSchedCon extends WUX.WDialog {
        protected fp: WUX.WFormPanel;
        constructor(id: string);
        protected updateState(nextState: any): void;
        getState(): object;
        protected onClickOk(): boolean;
    }
    class DlgSchedPar extends WUX.WDialog {
        protected fp: WUX.WFormPanel;
        constructor(id: string);
        protected updateState(nextState: any): void;
        getState(): object;
        protected onClickOk(): boolean;
    }
    class DlgSchedNot extends WUX.WDialog {
        protected fp: WUX.WFormPanel;
        constructor(id: string);
        protected updateState(nextState: any): void;
        getState(): object;
        protected onClickOk(): boolean;
    }
}
declare namespace GUI {
    class GUISchedulazioni extends WUX.WComponent {
        protected container: WUX.WContainer;
        protected tagsFilter: WUX.WTags;
        protected fpFilter: WUX.WFormPanel;
        protected btnFind: WUX.WButton;
        protected btnReset: WUX.WButton;
        protected btnNew: WUX.WButton;
        protected cntActions: AppTableActions;
        protected btnOpen: WUX.WButton;
        protected btnSave: WUX.WButton;
        protected btnCancel: WUX.WButton;
        protected btnDelete: WUX.WButton;
        protected btnToggle: WUX.WButton;
        protected tabResult: WUX.WDXTable;
        protected selId: any;
        protected tcoDetail: WUX.WTab;
        protected fpDetail: WUX.WFormPanel;
        protected selSerDet: LJSASelServizi;
        protected selAttDet: LJSASelAttivita;
        protected tabCon: WUX.WDXTable;
        protected btnAddCon: WUX.WButton;
        protected btnRemCon: WUX.WButton;
        protected tabPar: WUX.WDXTable;
        protected btnAddPar: WUX.WButton;
        protected btnRemPar: WUX.WButton;
        protected tabNot: WUX.WDXTable;
        protected btnAddNot: WUX.WButton;
        protected btnRemNot: WUX.WButton;
        protected dlgCon: DlgSchedCon;
        protected dlgPar: DlgSchedPar;
        protected dlgNot: DlgSchedNot;
        protected isNew: boolean;
        protected status: number;
        readonly iSTATUS_STARTUP = 0;
        readonly iSTATUS_VIEW = 1;
        readonly iSTATUS_EDITING = 2;
        constructor(id?: string);
        protected render(): WUX.WContainer;
        collapseHandler(e: JQueryEventObject): void;
        protected onSelect(): void;
        protected clearDet(): void;
        protected enableDet(e: boolean): void;
    }
}
declare namespace GUI {
    class GUIServizi extends WUX.WComponent {
        protected container: WUX.WContainer;
        protected tagsFilter: WUX.WTags;
        protected fpFilter: WUX.WFormPanel;
        protected btnFind: WUX.WButton;
        protected btnReset: WUX.WButton;
        protected btnNew: WUX.WButton;
        protected cntActions: AppTableActions;
        protected btnOpen: WUX.WButton;
        protected btnSave: WUX.WButton;
        protected btnCancel: WUX.WButton;
        protected btnDelete: WUX.WButton;
        protected tabResult: WUX.WDXTable;
        protected selId: any;
        protected tcoDetail: WUX.WTab;
        protected fpDetail: WUX.WFormPanel;
        protected isNew: boolean;
        protected status: number;
        readonly iSTATUS_STARTUP = 0;
        readonly iSTATUS_VIEW = 1;
        readonly iSTATUS_EDITING = 2;
        constructor(id?: string);
        protected render(): WUX.WContainer;
        collapseHandler(e: JQueryEventObject): void;
        protected onSelect(): void;
    }
}
