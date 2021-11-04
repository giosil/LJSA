namespace GUI {

    import WUtil = WUX.WUtil;

    export class DlgAttCon extends WUX.WDialog {
        protected fp: WUX.WFormPanel;

        constructor(id: string) {
            super(id, 'DlgAttCon');

            this.title = 'Configurazione';

            this.fp = new WUX.WFormPanel(this.subId('fp'));
            this.fp.addRow();
            this.fp.addTextField(IAtt.sCONF_OPZIONE, 'Opzione');
            this.fp.addRow();
            this.fp.addTextField(IAtt.sCONF_DESCRIZIONE, 'Descrizione');
            this.fp.addRow();
            this.fp.addTextField(IAtt.sCONF_VALORI, 'Valori');
            this.fp.addRow();
            this.fp.addTextField(IAtt.sCONF_PREDEFINITO, 'Predefinito');

            this.fp.setMandatory(IAtt.sCONF_OPZIONE, IAtt.sCONF_DESCRIZIONE);

            this.body
                .addRow()
                .addCol('12')
                .add(this.fp);
        }

        protected updateState(nextState: any) {
            super.updateState(nextState);
            if (this.fp) {
                this.fp.setState(this.state);
            }
        }

        getState(): object {
            if (this.fp) {
                this.state = this.fp.getState();
            }
            return this.state;
        }

        protected onClickOk(): boolean {
            let check = this.fp.checkMandatory(true);
            if (check) {
                WUX.showWarning('Specificare: ' + check);
                return false;
            }
            return true;
        }
    }

    export class DlgAttPar extends WUX.WDialog {
        protected fp: WUX.WFormPanel;

        constructor(id: string) {
            super(id, 'DlgAttPar');

            this.title = 'Parametro';

            this.fp = new WUX.WFormPanel(this.subId('fp'));
            this.fp.addRow();
            this.fp.addTextField(IAtt.sPAR_PARAMETRO, 'Parametro');
            this.fp.addRow();
            this.fp.addTextField(IAtt.sPAR_DESCRIZIONE, 'Descrizione');
            this.fp.addRow();
            this.fp.addTextField(IAtt.sPAR_VALORI, 'Valori');
            this.fp.addRow();
            this.fp.addTextField(IAtt.sPAR_PREDEFINITO, 'Predefinito');

            this.fp.setMandatory(IAtt.sPAR_PARAMETRO, IAtt.sPAR_DESCRIZIONE);

            this.body
                .addRow()
                .addCol('12')
                .add(this.fp);
        }

        protected updateState(nextState: any) {
            super.updateState(nextState);
            if (this.fp) {
                this.fp.setState(this.state);
            }
        }

        getState(): object {
            if (this.fp) {
                this.state = this.fp.getState();
            }
            return this.state;
        }

        protected onClickOk(): boolean {
            let check = this.fp.checkMandatory(true);
            if (check) {
                WUX.showWarning('Specificare: ' + check);
                return false;
            }
            return true;
        }
    }

    export class DlgAttNot extends WUX.WDialog {
        protected fp: WUX.WFormPanel;

        constructor(id: string) {
            super(id, 'DlgAttNot');

            this.title = 'Notifica';

            this.fp = new WUX.WFormPanel(this.subId('fp'));
            this.fp.addRow();
            this.fp.addComponent(IAtt.sNOT_EVENTO, 'Evento', new LJSASelEventi());
            this.fp.addRow();
            this.fp.addTextField(IAtt.sNOT_DESTINAZIONE, 'Destinazione');

            this.fp.setMandatory(IAtt.sNOT_EVENTO, IAtt.sNOT_DESTINAZIONE);

            this.body
                .addRow()
                .addCol('12')
                .add(this.fp);
        }

        protected updateState(nextState: any) {
            super.updateState(nextState);
            if (this.fp) {
                this.fp.setState(this.state);
            }
        }

        getState(): object {
            if (this.fp) {
                this.state = this.fp.getState();
            }
            return this.state;
        }

        protected onClickOk(): boolean {
            let check = this.fp.checkMandatory(true);
            if (check) {
                WUX.showWarning('Specificare: ' + check);
                return false;
            }
            return true;
        }
    }
}

