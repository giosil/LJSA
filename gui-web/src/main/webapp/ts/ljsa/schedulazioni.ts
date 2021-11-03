namespace GUI {

    import WUtil = WUX.WUtil;

    export class GUISchedulazioni extends WUX.WComponent {
        protected container: WUX.WContainer;
        protected tagsFilter: WUX.WTags;
        protected fpFilter: WUX.WFormPanel;
        protected btnFind: WUX.WButton;
        protected btnReset: WUX.WButton;
        // Nuovo
        protected btnNew: WUX.WButton;
        // Azioni base
        protected cntActions: AppTableActions;
        protected btnOpen: WUX.WButton;
        protected btnSave: WUX.WButton;
        protected btnCancel: WUX.WButton;
        protected btnDelete: WUX.WButton;
        // Risultato
        protected tabResult: WUX.WDXTable;
        protected selId: any;
        // Dettaglio
        protected tcoDetail: WUX.WTab;
        // Dettaglio attributi
        protected fpDetail: WUX.WFormPanel;
        protected selSerDet: LJSASelServizi;
        // Stati
        protected isNew: boolean;
        protected status: number;
        readonly iSTATUS_STARTUP = 0;
        readonly iSTATUS_VIEW = 1;
        readonly iSTATUS_EDITING = 2;

        constructor(id?: string) {
            super(id ? id : '*', 'GUISchedulazioni');
            this.status = this.iSTATUS_STARTUP;
        }

        protected render() {
            this.btnFind = new WUX.WButton(this.subId('bf'), GUI.TXT.FIND, '', WUX.BTN.SM_PRIMARY);
            this.btnFind.on('click', (e: JQueryEventObject) => {
                if (this.status == this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                let check = this.fpFilter.checkMandatory(true, true, false);
                if (check) {
                    WUX.showWarning('Specificare i seguenti campi: ' + check);
                    return;
                }
                let box = WUX.getComponent('boxFilter');
                if (box instanceof WUX.WBox) {
                    this.tagsFilter.setState(this.fpFilter.getValues(true));
                    box.collapse();
                }
                let user = GUI.getUserLogged();
                jrpc.execute('SCHEDULAZIONI.find', [this.fpFilter.getState(), user.groups], (result) => {
                    this.tabResult.setState(result);

                    this.fpDetail.clear();
                    this.status = this.iSTATUS_STARTUP;

                    if (this.selId) {
                        let idx = WUtil.indexOf(result, ISched.sID_SCHEDULAZIONE, this.selId);
                        if (idx >= 0) {
                            setTimeout(() => {
                                this.tabResult.select([idx]);
                            }, 100);
                        }
                        this.selId = null;
                    }
                });
            });

            this.btnReset = new WUX.WButton(this.subId('br'), GUI.TXT.RESET, '', WUX.BTN.SM_SECONDARY);
            this.btnReset.on('click', (e: JQueryEventObject) => {
                if (this.status == this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                this.fpFilter.clear();
                this.tagsFilter.setState({});
                this.tabResult.setState([]);
                this.fpDetail.clear();
                this.status = this.iSTATUS_STARTUP;
            });

            this.fpFilter = new WUX.WFormPanel(this.subId('ff'));
            this.fpFilter.addRow();
            this.fpFilter.addComponent(ISched.sID_SERVIZIO, 'Servizio', new LJSASelServizi());
            this.fpFilter.addTextField(ISched.sID_ATTIVITA, 'Attivita\'');
            this.fpFilter.addComponent(ISched.sSTATO,       'Stato',    new LJSASelStati());
            this.fpFilter.addRow();
            this.fpFilter.addTextField(ISched.sID_CREDENZIALE_INS, 'Cred. Ins.');
            this.fpFilter.addDateField(ISched.sINIZIO_VALIDITA,    'Inizio Val.');
            this.fpFilter.addDateField(ISched.sFINE_VALIDITA,      'Fine Val.');

            this.selSerDet = new LJSASelServizi();

            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addComponent(ISched.sID_SERVIZIO,     'Servizio', this.selSerDet);
            this.fpDetail.addTextField(ISched.sID_ATTIVITA,     'Attivita\'');
            this.fpDetail.addRow();
            this.fpDetail.addTextField(ISched.sDESCRIZIONE,     'Descrizione');
            this.fpDetail.addTextField(ISched.sSCHEDULAZIONE,   'Schedulazione');
            this.fpDetail.addRow();
            this.fpDetail.addDateField(ISched.sINIZIO_VALIDITA, 'Inizio Val.');
            this.fpDetail.addDateField(ISched.sFINE_VALIDITA,   'Fine Val.');
            this.fpDetail.addInternalField(ISched.sID_SCHEDULAZIONE);
            this.fpDetail.addInternalField(ISched.sSTATO);
            this.fpDetail.addInternalField(ISched.sID_CREDENZIALE_INS);
            this.fpDetail.addInternalField(ISched.sDATA_INS);
            this.fpDetail.addInternalField(ISched.sORA_INS);
            this.fpDetail.addInternalField(ISched.sESEC_COMPLETATE);
            this.fpDetail.addInternalField(ISched.sESEC_INTERROTTE);
            this.fpDetail.enabled = false;

            this.fpFilter.onEnterPressed((e: WUX.WEvent) => {
                this.btnFind.trigger('click');
            });

            this.btnNew = new WUX.WButton(this.subId('bn'), GUI.TXT.NEW, '', WUX.BTN.SM_INFO);
            this.btnNew.on('click', (e: JQueryEventObject) => {
                if (this.status == this.iSTATUS_EDITING) {
                    this.btnNew.blur();
                    return;
                }

                this.isNew = true;
                this.status = this.iSTATUS_EDITING;
                this.selId = null;

                this.tabResult.clearSelection();

                this.fpDetail.enabled = true;

                this.fpDetail.clear();
                this.selSerDet.setState(_defService);

                setTimeout(() => { this.fpDetail.focus(); }, 100);
            });
            this.btnOpen = new WUX.WButton(this.subId('bo'), GUI.TXT.OPEN, GUI.ICO.OPEN, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnOpen.on('click', (e: JQueryEventObject) => {
                if (this.status == this.iSTATUS_EDITING || this.status == this.iSTATUS_STARTUP) {
                    this.btnOpen.blur();
                    return;
                }
                let sr = this.tabResult.getSelectedRows();
                if (!sr || !sr.length) {
                    WUX.showWarning('Seleziona l\'elemento da modificare');
                    this.btnOpen.blur();
                    return;
                }
                this.isNew = false;
                this.status = this.iSTATUS_EDITING;
                this.selId = null;

                this.fpDetail.enabled = true;

                setTimeout(() => { this.fpDetail.focus(); }, 100);
            });
            this.btnSave = new WUX.WButton(this.subId('bs'), GUI.TXT.SAVE, GUI.ICO.SAVE, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnSave.on('click', (e: JQueryEventObject) => {
                if (this.status != this.iSTATUS_EDITING) {
                    WUX.showWarning('Cliccare su Modifica.');
                    this.btnSave.blur();
                    return;
                }
                let check = this.fpDetail.checkMandatory(true);
                if (check) {
                    this.btnSave.blur();
                    WUX.showWarning('Specificare: ' + check);
                    return;
                }

                let values = this.fpDetail.getState();

                if (this.isNew) {
                    jrpc.execute('SCHEDULAZIONI.insert', [values], (result) => {
                        this.status = this.iSTATUS_VIEW;

                        this.fpDetail.enabled = false;

                        this.selId = result[ISched.sID_SCHEDULAZIONE];
                        this.btnFind.trigger('click');
                    });
                }
                else {
                    jrpc.execute('SCHEDULAZIONI.update', [values], (result) => {
                        this.status = this.iSTATUS_VIEW;

                        this.fpDetail.enabled = false;

                        this.selId = result[ISched.sID_SCHEDULAZIONE];
                        let selRows = this.tabResult.getSelectedRows();
                        if (!selRows || !selRows.length) {
                            this.btnFind.trigger('click');
                        }
                        else {
                            let idx = selRows[0];
                            let records = this.tabResult.getState();
                            records[idx] = result;
                            this.tabResult.refresh();
                            setTimeout(() => {
                                this.tabResult.select([idx]);
                            }, 100);
                        }
                    });
                }
            });
            this.btnCancel = new WUX.WButton(this.subId('bc'), GUI.TXT.CANCEL, GUI.ICO.CANCEL, WUX.BTN.ACT_OUTLINE_INFO);
            this.btnCancel.on('click', (e: JQueryEventObject) => {
                if (this.status != this.iSTATUS_EDITING) {
                    this.btnCancel.blur();
                    return;
                }
                WUX.confirm(GUI.MSG.CONF_CANCEL, (res: any) => {
                    if (!res) return;
                    if (this.isNew) {
                        this.fpDetail.clear();
                    }
                    else {
                        this.onSelect();
                    }
                    this.status = this.iSTATUS_VIEW;

                    this.fpDetail.enabled = false;

                    this.selId = null;
                });
            });
            this.btnDelete = new WUX.WButton(this.subId('bd'), GUI.TXT.DELETE, GUI.ICO.DELETE, WUX.BTN.ACT_OUTLINE_DANGER);
            this.btnDelete.on('click', (e: JQueryEventObject) => {
                this.selId = null;
                this.btnDelete.blur();
                if (this.status == this.iSTATUS_EDITING || this.status == this.iSTATUS_STARTUP) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                let rd = this.tabResult.getSelectedRowsData();
                if (!rd || !rd.length) return;
                let ids = WUtil.getString(rd[0], ISched.sID_SERVIZIO);
                let idc = WUtil.getNumber(rd[0], ISched.sID_SCHEDULAZIONE);
                let usr = GUI.getUserLogged().userName;
                WUX.confirm(GUI.MSG.CONF_DELETE, (res: any) => {
                    if (!res) return;
                    jrpc.execute('SCHEDULAZIONI.delete', [ids, idc, usr], (result) => {
                        this.btnFind.trigger('click');
                    });
                });
            });

            let rc = [
                ['Id', ISched.sID_SCHEDULAZIONE],
                ['Servizio', ISched.sID_SERVIZIO],
                ['Attivita\'', ISched.sID_ATTIVITA],
                ['Schedulazione', ISched.sSCHEDULAZIONE],
                ['Stato', ISched.sSTATO],
                ['Descrizione', ISched.sDESCRIZIONE],
                ['Esec. Compl.', ISched.sESEC_COMPLETATE],
                ['Esec. Int.', ISched.sESEC_INTERROTTE],
                ['Id Cred. Ins.', ISched.sID_CREDENZIALE_INS],
                ['Data Ins.', ISched.sDATA_INS],
                ['Ora Ins.', ISched.sORA_INS],
                ['Id Cred. Agg.', ISched.sID_CREDENZIALE_AGG],
                ['Data Agg.', ISched.sDATA_AGG],
                ['Ora Agg.', ISched.sORA_AGG]
            ];
            this.tabResult = new WUX.WDXTable(this.subId('tr'), WUtil.col(rc, 0), WUtil.col(rc, 1));
            this.tabResult.css({ h: 220 });
            this.tabResult.widths = [100];
            this.tabResult.onSelectionChanged((e: { element?: JQuery, selectedRowsData?: Array<any> }) => {

                this.onSelect();

            });
            this.tabResult.onRowPrepared((e: { element?: JQuery, rowElement?: JQuery, data?: any, rowIndex?: number, isSelected?: boolean }) => {
                if (!e.data) return;
                let s = WUtil.getString(e.data, ISched.sSTATO);
                if (s == 'D') {
                    WUX.setCss(e.rowElement, WUX.CSS.ERROR);
                }
                else if (s == 'E') {
                    WUX.setCss(e.rowElement, WUX.CSS.SUCCESS);
                }
            });

            this.cntActions = new AppTableActions('ta');
            this.cntActions.left.add(this.btnOpen);
            this.cntActions.left.add(this.btnDelete);
            this.cntActions.left.add(this.btnSave);
            this.cntActions.left.add(this.btnCancel);
            this.cntActions.right.add(this.btnNew);

            this.tagsFilter = new WUX.WTags('tf');

            this.tcoDetail = new WUX.WTab('tcod');
            this.tcoDetail.addTab('Attributi', WUX.WIcon.ADDRESS_CARD)
                .addRow()
                .addCol('12', { h: 300 })
                .add(this.fpDetail);

            this.container = new WUX.WContainer();
            this.container.attributes = WUX.ATT.STICKY_CONTAINER;
            this.container
                .addBox('Filtri di ricerca:', '', '', 'boxFilter', WUX.ATT.BOX_FILTER)
                .addTool(this.tagsFilter)
                .addCollapse(this.collapseHandler.bind(this))
                .addRow()
                .addCol('col-xs-11 b-r')
                .add(this.fpFilter)
                .addCol('col-xs-1 b-l')
                .addGroup({ classStyle: 'form-group text-right' }, this.btnFind, this.btnReset)
                .end() // end Box
                .addBox()
                .addGroup({ classStyle: 'search-actions-and-results-wrapper' }, this.cntActions, this.tabResult)
                .end() // end Box
                .addRow()
                .addCol('12').section('Dettaglio')
                .add(this.tcoDetail);

            return this.container;
        }

        collapseHandler(e: JQueryEventObject) {
            let c = WUtil.getBoolean(e.data, 'collapsed');
            if (c) {
                this.tagsFilter.setState({});
            }
            else {
                this.tagsFilter.setState(this.fpFilter.getValues(true));
            }
        }

        protected onSelect(): void {
            var item = WUtil.getItem(this.tabResult.getSelectedRowsData(), 0);
            if (!item) return;

            let id = WUtil.getNumber(item, ISched.sID_SCHEDULAZIONE);
            if (!id) return;

            this.fpDetail.clear();

            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.fpDetail.enabled = false;
            }

            jrpc.execute('SCHEDULAZIONI.read', [id], (result) => {
                this.fpDetail.setState(result);
                this.status = this.iSTATUS_VIEW;
            });
        }
    }
}