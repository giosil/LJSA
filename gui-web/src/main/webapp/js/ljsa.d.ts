declare namespace GUI {
    class GUIAttivita extends WUX.WComponent {
        constructor(id?: string);
        protected render(): string;
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
        constructor(id?: string, multiple?: boolean);
        protected componentDidMount(): void;
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
    class GUILog extends WUX.WComponent {
        constructor(id?: string);
        protected render(): string;
    }
}
declare namespace GUI {
    class GUISchedulatore extends WUX.WComponent {
        constructor(id?: string);
        protected render(): string;
    }
}
declare namespace GUI {
    class GUISchedulazioni extends WUX.WComponent {
        constructor(id?: string);
        protected render(): string;
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
