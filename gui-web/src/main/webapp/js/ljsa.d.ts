declare namespace GUI {
    class GUIAttivita extends WUX.WComponent {
        constructor(id?: string);
        protected render(): string;
    }
}
declare namespace GUI {
    let LJSA_SERVICE: string;
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
    class IServizio {
        static sID_SERVIZIO: string;
        static sDESCRIZIONE: string;
        static sATTIVO: string;
        static sCREDENZIALI: string;
    }
}
declare var jrpc: JRPC;
declare namespace GUI {
    class GUIClassi extends WUX.WComponent {
        constructor(id?: string);
        protected render(): string;
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
}
declare namespace GUI {
    class GUICredenziali extends WUX.WComponent {
        constructor(id?: string);
        protected render(): string;
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
        constructor(id?: string);
        protected render(): string;
    }
}
