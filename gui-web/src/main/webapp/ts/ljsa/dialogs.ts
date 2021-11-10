namespace GUI {

    import WUtil = WUX.WUtil;

    export let _c = [
        ['attachFiles','Se S i file prodotti vengono inviati in allegato alla mail'],
        ['attachErrorFiles','Se S i file di errore prodotti vengono inviati in allegato alla mail'],
        ['compressFiles','Se S i file prodotti vengono compressi'],
        ['excludeHolidays','Se S vengono esclusi i giorni festivi'],
        ['fileInfo','Se S viene creato il file di informazioni predefinito'],
        ['jdbc.driver','Driver jdbc'],
        ['jdbc.ds','Data Source jdbc'],
        ['jdbc.url','URL jdbc'],
        ['jdbc.user','User jdbc'],
        ['jdbc.password','Passoword jdbc'],
        ['language','Lingua'],
        ['mail.delete','Se S le mail non vengono conservate sul server'],
        ['mail.user','Utente casella di posta elettronica'],
        ['mail.password','Password casella di posta elettronica'],
        ['message','Testo del messaggio di notifica'],
        ['nolog','Se S le elaborazioni NON vengono tracciate in archivio'],
        ['report','Template del report'],
        ['single','Se S si bloccano esecuzioni sovrapposte dello stesso job'],
        ['stopOnTimeout','Interrompe l\'elaborazione a timeout raggiunto'],
        ['subject','Oggetto del messaggio di notifica'],
        ['timeout','Timeout di elaborazione espresso in minuti']
    ];

    export let _p = [
        ['command','Comando di sistema'],
        ['exception','[LJTest] Simulazione di eccezione'],
        ['fromDate','Dalla data (YYYYMMDD)'],
        ['name','Nome'],
        ['sleep','[LJTest] Simulazione elaborazione (s)'],
        ['sql','Comando sql'],
        ['sql.1','Comando sql 1a parte'],
        ['sql.2','Comando sql 2a parte'],
        ['sql.3','Comando sql 3a parte'],
        ['sql.4','Comando sql 4a parte'],
        ['sql.5','Comando sql 5a parte'],
        ['sql.6','Comando sql 6a parte'],
        ['sql.7','Comando sql 7a parte'],
        ['table','Nome tabella'],
        ['text','Testo'],
        ['title','Titolo report'],
        ['toDate','Alla data (YYYYMMDD)'],
        ['type','Tipo report'],
    ];

    export function _c1(a: any[][], k: string): string {
        if(!a || !k) return;
        for(let i = 0; i < a.length; i++) {
            if(a[i][0] == k) return a[i][1];
        }
        return '';
    }

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

            this.fp.onFocus(IAtt.sCONF_OPZIONE, (e: JQueryEventObject) => {
                $(e.target).autocomplete({
                    source: WUtil.col(_c, 0),
                    minLength: 1
                });
            });
            this.fp.onFocus(IAtt.sCONF_DESCRIZIONE, (e: JQueryEventObject) => {
                if(this.fp.isBlank(IAtt.sCONF_DESCRIZIONE)) {
                    let k = this.fp.getValue(IAtt.sCONF_OPZIONE);
                    let d = _c1(_c, k);
                    if(d) {
                       setTimeout(() => {
                           this.fp.setValue(IAtt.sCONF_DESCRIZIONE, d);
                       });
                    }
                }
            });

            this.body
                .addRow()
                .addCol('12')
                .add(this.fp);
        }

        protected updateState(nextState: any) {
            super.updateState(nextState);
            if (this.fp) {
                this.fp.clear();
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

            this.fp.onFocus(IAtt.sPAR_PARAMETRO, (e: JQueryEventObject) => {
                $(e.target).autocomplete({
                    source: WUtil.col(_p, 0),
                    minLength: 1
                });
            });
            this.fp.onFocus(IAtt.sPAR_DESCRIZIONE, (e: JQueryEventObject) => {
                if(this.fp.isBlank(IAtt.sPAR_DESCRIZIONE)) {
                    let k = this.fp.getValue(IAtt.sPAR_PARAMETRO);
                    let d = _c1(_p, k);
                    if(d) {
                       setTimeout(() => {
                           this.fp.setValue(IAtt.sPAR_DESCRIZIONE, d);
                       });
                    }
                }
            });

            this.body
                .addRow()
                .addCol('12')
                .add(this.fp);
        }

        protected updateState(nextState: any) {
            super.updateState(nextState);
            if (this.fp) {
                this.fp.clear();
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
                this.fp.clear();
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

    export class DlgSchedCon extends WUX.WDialog {
        protected fp: WUX.WFormPanel;

        constructor(id: string) {
            super(id, 'DlgSchedCon');

            this.title = 'Configurazione';

            this.fp = new WUX.WFormPanel(this.subId('fp'));
            this.fp.addRow();
            this.fp.addTextField(ISched.sCONF_OPZIONE, 'Opzione');
            this.fp.addRow();
            this.fp.addTextField(ISched.sCONF_DESCRIZIONE, 'Descrizione', true);
            this.fp.addRow();
            this.fp.addTextField(ISched.sCONF_VALORI, 'Valori', true);
            this.fp.addRow();
            this.fp.addTextField(ISched.sCONF_VALORE, 'Valore');

            this.fp.setMandatory(ISched.sCONF_OPZIONE, ISched.sCONF_DESCRIZIONE);

            this.fp.onFocus(ISched.sCONF_OPZIONE, (e: JQueryEventObject) => {
                $(e.target).autocomplete({
                    source: WUtil.col(_c, 0),
                    minLength: 1
                });
            });
            this.fp.onFocus(ISched.sCONF_DESCRIZIONE, (e: JQueryEventObject) => {
                if(this.fp.isBlank(ISched.sCONF_DESCRIZIONE)) {
                    let k = this.fp.getValue(ISched.sCONF_OPZIONE);
                    let d = _c1(_c, k);
                    if(d) {
                       setTimeout(() => {
                           this.fp.setValue(ISched.sCONF_DESCRIZIONE, d);
                       });
                    }
                }
            });

            this.body
                .addRow()
                .addCol('12')
                .add(this.fp);
        }

        protected updateState(nextState: any) {
            super.updateState(nextState);
            if (this.fp) {
                this.fp.clear();
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

    export class DlgSchedPar extends WUX.WDialog {
        protected fp: WUX.WFormPanel;

        constructor(id: string) {
            super(id, 'DlgSchedPar');

            this.title = 'Parametro';

            this.fp = new WUX.WFormPanel(this.subId('fp'));
            this.fp.addRow();
            this.fp.addTextField(ISched.sPAR_PARAMETRO, 'Parametro');
            this.fp.addRow();
            this.fp.addTextField(ISched.sPAR_DESCRIZIONE, 'Descrizione', true);
            this.fp.addRow();
            this.fp.addTextField(ISched.sPAR_VALORI, 'Valori', true);
            this.fp.addRow();
            this.fp.addTextField(ISched.sPAR_VALORE, 'Valore');

            this.fp.setMandatory(ISched.sPAR_PARAMETRO, ISched.sPAR_DESCRIZIONE);

            this.fp.onFocus(ISched.sPAR_PARAMETRO, (e: JQueryEventObject) => {
                $(e.target).autocomplete({
                    source: WUtil.col(_p, 0),
                    minLength: 1
                });
            });
            this.fp.onFocus(ISched.sPAR_DESCRIZIONE, (e: JQueryEventObject) => {
                if(this.fp.isBlank(ISched.sPAR_DESCRIZIONE)) {
                    let k = this.fp.getValue(ISched.sPAR_PARAMETRO);
                    let d = _c1(_p, k);
                    if(d) {
                       setTimeout(() => {
                           this.fp.setValue(ISched.sPAR_DESCRIZIONE, d);
                       });
                    }
                }
            });

            this.body
                .addRow()
                .addCol('12')
                .add(this.fp);
        }

        protected updateState(nextState: any) {
            super.updateState(nextState);
            if (this.fp) {
                this.fp.clear();
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

    export class DlgSchedNot extends WUX.WDialog {
        protected fp: WUX.WFormPanel;

        constructor(id: string) {
            super(id, 'DlgSchedNot');

            this.title = 'Notifica';

            this.fp = new WUX.WFormPanel(this.subId('fp'));
            this.fp.addRow();
            this.fp.addComponent(ISched.sNOT_EVENTO, 'Evento', new LJSASelEventi());
            this.fp.addRow();
            this.fp.addTextField(ISched.sNOT_DESTINAZIONE, 'Destinazione');

            this.fp.setMandatory(ISched.sNOT_EVENTO, ISched.sNOT_DESTINAZIONE);

            this.body
                .addRow()
                .addCol('12')
                .add(this.fp);
        }

        protected updateState(nextState: any) {
            super.updateState(nextState);
            if (this.fp) {
                this.fp.clear();
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

    export class DlgSchedLog extends WUX.WDialog<number, any[]> {
        lblMain: WUX.WLabel;
        tabData: WUX.WDXTable;
        
        constructor(id: string) {
            super(id, 'DlgSchedLog');

            this.title = 'Log Schedulazione';

            this.lblMain = new WUX.WLabel(this.subId('lm'));
            this.lblMain.css(WUX.CSS.LABEL_INFO);
            
            let dc = [
                ['Id Log', ILog.sID_LOG, 's'],
                ['Data Inizio', ILog.sDATA_INIZIO, 'd'],
                ['Ora Inizio', ILog.sORA_INIZIO, 's'],
                ['Data Fine', ILog.sDATA_FINE, 'd'],
                ['Ora Fine', ILog.sORA_FINE, 's'],
                ['Stato', ILog.sSTATO, 's'],
                ['Rapporto', ILog.sRAPPORTO , 's']
            ];

            this.tabData = new WUX.WDXTable(this.subId('tac'), WUtil.col(dc, 0), WUtil.col(dc, 1));
            this.tabData.types = WUtil.col(dc, 2);
            this.tabData.css({ h: 400 });
            this.tabData.exportFile = 'log_schedulazione';
            this.tabData.onCellPrepared((e: { component?: DevExpress.DOMComponent, element?: DevExpress.core.dxElement, model?: any, data?: any, key?: any, value?: any, displayValue?: string, text?: string, columnIndex?: number, column?: DevExpress.ui.dxDataGridColumn, rowIndex?: number, rowType?: string, row?: DevExpress.ui.dxDataGridRowObject, isSelected?: boolean, isExpanded?: boolean, cellElement?: DevExpress.core.dxElement }) => {
                let f = e.column.dataField;
                if (f == ILog.sID_LOG) {
                    e.cellElement.addClass('clickable');
                }
            });
            this.tabData.onCellClick((e: { component?: DevExpress.DOMComponent, element?: DevExpress.core.dxElement, model?: any, jQueryEvent?: JQueryEventObject, event?: DevExpress.event, data?: any, key?: any, value?: any, displayValue?: string, text?: string, columnIndex?: number, column?: any, rowIndex?: number, rowType?: string, cellElement?: DevExpress.core.dxElement, row?: DevExpress.ui.dxDataGridRowObject }) => {
                let row = e.row;
                if (row != null && row.rowType == 'data') {
                    let f = e.column.dataField;
                    if (f == ILog.sID_LOG) {
                        let u = WUtil.getString(e.data, ILog.sFILES);
                        if(!u) {
                            WUX.showWarning("File non disponibili");
                        }
                        else {
                            WUX.openURL(u, false, true);
                        }
                    }
                }
            });

            this.body
                .addRow()
                .addCol('12', { a: 'right' })
                .add(this.lblMain)
                .addDiv(8)
                .addRow()
                .addCol('12')
                .add(this.tabData);
        }

        protected updateProps(nextProps: number): void {
            super.updateProps(nextProps);
            if (this.lblMain) {
                if (this.props) {
                    this.lblMain.setState('Id Schedulazione: ' + this.props);
                }
                else {
                    this.lblMain.setState('');
                }
            }
        }

        protected updateState(nextState: any[]): void {
            super.updateState(nextState);
            if (this.tabData) {
                this.tabData.setState(this.state);
            }
        }

        getState(): any[] {
            if (this.tabData) {
                this.state = this.tabData.getState();
            }
            return this.state;
        }

        protected onShown() {
            this.tabData.scrollTo(0);
            setTimeout(() => {
                if (this.state && this.state.length) {
                    this.tabData.refresh();
                }
                else {
                    this.tabData.repaint();
                }
            }, 100);
        }

        protected componentDidMount(): void {
            super.componentDidMount();
            let w = $(window).width();
            if (w > 1260) {
                this.cntMain.css({ w: 1260, h: 600 });
            }
            else {
                this.cntMain.css({ w: 1000, h: 600 });
            }
        }
    }
}

