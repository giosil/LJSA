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
        protected btnToggle: WUX.WButton;
        // Risultato
        protected tabResult: WUX.WDXTable;
        protected selId: any;
        // Dettaglio
        protected tcoDetail: WUX.WTab;
        // Dettaglio attributi
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
        // Dialogs
        protected dlgCon: DlgSchedCon;
        protected dlgPar: DlgSchedPar;
        protected dlgNot: DlgSchedNot;
        // Stati
        protected isNew: boolean;
        protected status: number;
        readonly iSTATUS_STARTUP = 0;
        readonly iSTATUS_VIEW = 1;
        readonly iSTATUS_EDITING = 2;

        constructor(id?: string) {
            super(id ? id : '*', 'GUISchedulazioni');
            this.status = this.iSTATUS_STARTUP;

            this.dlgCon = new DlgSchedCon(this.subId('dlgac'));
            this.dlgCon.onHiddenModal((e: JQueryEventObject) => {
                if (!this.dlgCon.ok) return;
                let d = this.tabCon.getState();
                let s = this.dlgCon.getState();
                let i = WUtil.indexOf(d, ISched.sCONF_OPZIONE, s[ISched.sCONF_OPZIONE]);
                if(i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                this.tabCon.setState(d);
            });
            this.dlgPar = new DlgSchedPar(this.subId('dlgap'));
            this.dlgPar.onHiddenModal((e: JQueryEventObject) => {
                if (!this.dlgPar.ok) return;
                let d = this.tabPar.getState();
                let s = this.dlgPar.getState();
                let i = WUtil.indexOf(d, ISched.sPAR_PARAMETRO, s[ISched.sPAR_PARAMETRO]);
                if(i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                this.tabPar.setState(d);
            });
            this.dlgNot = new DlgSchedNot(this.subId('dlgan'));
            this.dlgNot.onHiddenModal((e: JQueryEventObject) => {
                if (!this.dlgNot.ok) return;
                let d = this.tabNot.getState();
                let s = this.dlgNot.getState();
                let i = GUI.indexOf(d, ISched.sNOT_EVENTO, ISched.sNOT_DESTINAZIONE, s[ISched.sCONF_OPZIONE] + ':' + s[ISched.sNOT_DESTINAZIONE]);
                if(i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                this.tabNot.setState(d);
            });
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

                    this.clearDet();
                    this.status = this.iSTATUS_STARTUP;

                    if (this.selId) {
                        let idx = WUtil.indexOf(result, ISched.sID, this.selId);
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
                this.clearDet();
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
            this.selAttDet = new LJSASelAttivita();
            this.selSerDet.on('statechange', (e: WUX.WEvent) => {
                this.selAttDet.service = this.selSerDet.getState();
            });

            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addComponent(ISched.sID_SERVIZIO,     'Servizio', this.selSerDet);
            this.fpDetail.addComponent(ISched.sID_ATTIVITA,     'Attivita\'', this.selAttDet);
            this.fpDetail.addRow();
            this.fpDetail.addTextField(ISched.sDESCRIZIONE,     'Descrizione');
            this.fpDetail.addTextField(ISched.sSCHEDULAZIONE,   'Schedulazione');
            this.fpDetail.addRow();
            this.fpDetail.addDateField(ISched.sINIZIO_VALIDITA, 'Inizio Val.');
            this.fpDetail.addDateField(ISched.sFINE_VALIDITA,   'Fine Val.');
            this.fpDetail.addInternalField(ISched.sID);
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

                this.enableDet(true);

                this.clearDet();
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

                this.enableDet(true);

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
                values[ISched.sCONFIGURAZIONE] = this.tabCon.getState();
                values[ISched.sPARAMETRI] = this.tabPar.getState();
                values[ISched.sNOTIFICA] = this.tabNot.getState();
                GUI.putUserLog(values);

                if (this.isNew) {
                    jrpc.execute('SCHEDULAZIONI.insert', [values], (result) => {
                        this.status = this.iSTATUS_VIEW;

                        this.enableDet(false);

                        this.selId = result[ISched.sID];
                        this.btnFind.trigger('click');
                    });
                }
                else {
                    jrpc.execute('SCHEDULAZIONI.update', [values], (result) => {
                        this.status = this.iSTATUS_VIEW;

                        this.enableDet(false);

                        this.selId = result[ISched.sID];
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
                        this.clearDet();
                    }
                    else {
                        this.onSelect();
                    }
                    this.status = this.iSTATUS_VIEW;

                    this.enableDet(false);

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
                let idc = WUtil.getNumber(rd[0], ISched.sID);
                let usr = GUI.getUserLogged().userName;
                WUX.confirm(GUI.MSG.CONF_DELETE, (res: any) => {
                    if (!res) return;
                    jrpc.execute('SCHEDULAZIONI.delete', [ids, idc, usr], (result) => {
                        this.btnFind.trigger('click');
                    });
                });
            });
            this.btnToggle = new WUX.WButton(this.subId('bt'), 'Abilita', WUX.WIcon.THUMBS_O_UP, WUX.BTN.ACT_OUTLINE_DANGER);
            this.btnToggle.on('click', (e: JQueryEventObject) => {
                this.btnToggle.blur();
                let rd = this.tabResult.getSelectedRowsData();
                if (!rd || !rd.length) {
                    WUX.showWarning('Selezione l\'elemento da abilitare/disabilitare');
                    return;
                }
                let ids = WUtil.getString(rd[0], ISched.sID_SERVIZIO);
                let idc = WUtil.getNumber(rd[0], ISched.sID);
                let cst = WUtil.getString(rd[0], ISched.sSTATO);
                let flg = !(cst == 'D');
                let usr = GUI.getUserLogged().userName;
                this.selId = idc;
                jrpc.execute('SCHEDULAZIONI.setEnabled', [ids, idc, flg, usr], (result) => {
                    let sr = this.tabResult.getSelectedRows();
                    if (!sr || !sr.length) {
                        this.btnFind.trigger('click');
                    }
                    else {
                        let r = this.tabResult.getState();
                        let x = sr[0];
                        if (flg) {
                            r[x][ISched.sSTATO] = 'A';
                            this.btnToggle.setText('Disabilita', WUX.WIcon.THUMBS_O_DOWN);
                            this.btnToggle.setText('Disabilita', WUX.WIcon.THUMBS_O_DOWN);
                        }
                        else {
                            r[x][ISched.sSTATO] = 'D';
                            this.btnToggle.setText('Abilita', WUX.WIcon.THUMBS_O_UP);
                            this.btnToggle.setText('Abilita', WUX.WIcon.THUMBS_O_UP);
                        }
                        this.tabResult.refresh();
                        setTimeout(() => {
                            this.tabResult.select([x]);
                        }, 100);
                    }
                });
            });

            let rc = [
                ['Id', ISched.sID],
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

            this.tabCon = new WUX.WDXTable(this.subId('tbc'), ['Opzione', 'Descrizione', 'Valore'], [ISched.sCONF_OPZIONE, ISched.sCONF_DESCRIZIONE, ISched.sCONF_VALORE]);
            this.tabCon.selectionMode = 'single';
            this.tabCon.css({ h: 240 });
            this.tabCon.onDoubleClick((e: { element?: JQuery }) => {
                let s = this.tabCon.getSelectedRowsData();
                if (!s || !s.length) return;
                this.dlgCon.setState(s[0]);
                this.dlgCon.show(this);
            });

            this.btnAddCon = new WUX.WButton(this.subId('bac'), GUI.TXT.ADD, '', WUX.BTN.SM_PRIMARY);
            this.btnAddCon.on('click', (e: JQueryEventObject) => {
                this.dlgCon.setState(null);
                this.dlgCon.show(this);
            });
            this.btnRemCon = new WUX.WButton(this.subId('brc'), GUI.TXT.REMOVE, '', WUX.BTN.SM_DANGER);
            this.btnRemCon.on('click', (e: JQueryEventObject) => {
                let s = this.tabCon.getSelectedRows();
                if (!s || !s.length) return;
                let d = this.tabCon.getState();
                d.splice(s[0], 1);
                this.tabCon.setState(d);
            });

            this.tabPar = new WUX.WDXTable(this.subId('tbp'), ['Parametro', 'Descrizione', 'Valore'], [ISched.sPAR_PARAMETRO, ISched.sPAR_DESCRIZIONE, ISched.sPAR_VALORE]);
            this.tabPar.selectionMode = 'single';
            this.tabPar.css({ h: 240 });
            this.tabPar.onDoubleClick((e: { element?: JQuery }) => {
                let s = this.tabPar.getSelectedRowsData();
                if (!s || !s.length) return;
                this.dlgPar.setState(s[0]);
                this.dlgPar.show(this);
            });

            this.btnAddPar = new WUX.WButton(this.subId('bap'), GUI.TXT.ADD, '', WUX.BTN.SM_PRIMARY);
            this.btnAddPar.on('click', (e: JQueryEventObject) => {
                this.dlgPar.setState(null);
                this.dlgPar.show(this);
            });
            this.btnRemPar = new WUX.WButton(this.subId('brp'), GUI.TXT.REMOVE, '', WUX.BTN.SM_DANGER);
            this.btnRemPar.on('click', (e: JQueryEventObject) => {
                let s = this.tabPar.getSelectedRows();
                if (!s || !s.length) return;
                let d = this.tabPar.getState();
                d.splice(s[0], 1);
                this.tabPar.setState(d);
            });

            this.tabNot = new WUX.WDXTable(this.subId('tbn'), ['Evento', 'Destinazione'], [ISched.sNOT_EVENTO, ISched.sNOT_DESTINAZIONE]);
            this.tabNot.selectionMode = 'single';
            this.tabNot.css({ h: 240 });
            this.tabNot.onDoubleClick((e: { element?: JQuery }) => {
                let s = this.tabPar.getSelectedRowsData();
                if (!s || !s.length) return;
                this.dlgNot.setState(s[0]);
                this.dlgNot.show(this);
            });

            this.btnAddNot = new WUX.WButton(this.subId('ban'), GUI.TXT.ADD, '', WUX.BTN.SM_PRIMARY);
            this.btnAddNot.on('click', (e: JQueryEventObject) => {
                this.dlgNot.setState(null);
                this.dlgNot.show(this);
            });
            this.btnRemNot = new WUX.WButton(this.subId('brn'), GUI.TXT.REMOVE, '', WUX.BTN.SM_DANGER);
            this.btnRemNot.on('click', (e: JQueryEventObject) => {
                let s = this.tabNot.getSelectedRows();
                if (!s || !s.length) return;
                let d = this.tabNot.getState();
                d.splice(s[0], 1);
                this.tabNot.setState(d);
            });

            this.cntActions = new AppTableActions('ta');
            this.cntActions.left.add(this.btnOpen);
            this.cntActions.left.add(this.btnDelete);
            this.cntActions.left.add(this.btnSave);
            this.cntActions.left.add(this.btnCancel);
            this.cntActions.left.add(this.btnToggle);
            this.cntActions.right.add(this.btnNew);

            this.tagsFilter = new WUX.WTags('tf');

            this.tcoDetail = new WUX.WTab('tcod');
            this.tcoDetail.addTab('Attributi', WUX.WIcon.ADDRESS_CARD)
                .addRow()
                .addCol('12', { h: 300 })
                .add(this.fpDetail);

            this.tcoDetail.addTab('Configurazione', WUX.WIcon.WRENCH)
                .addRow()
                .addCol('11', { h: 300 })
                .add(this.tabCon)
                .addCol('1', { h: 300 })
                .addStack(WUX.CSS.STACK_BTNS, this.btnAddCon, this.btnRemCon);

            this.tcoDetail.addTab('Parametri', WUX.WIcon.EDIT)
                .addRow()
                .addCol('11', { h: 300 })
                .add(this.tabPar)
                .addCol('1', { h: 300 })
                .addStack(WUX.CSS.STACK_BTNS, this.btnAddPar, this.btnRemPar);

            this.tcoDetail.addTab('Notifica', WUX.WIcon.ENVELOPE_O)
                .addRow()
                .addCol('11', { h: 300 })
                .add(this.tabNot)
                .addCol('1', { h: 300 })
                .addStack(WUX.CSS.STACK_BTNS, this.btnAddNot, this.btnRemNot);

            this.tcoDetail.on('statechange', (e: WUX.WEvent) => {
                let itab = this.tcoDetail.getState();
                switch (itab) {
                    case 0:
                        break;
                    case 1:
                        this.tabCon.repaint();
                        break;
                    case 2:
                        this.tabPar.repaint();
                        break;
                    case 3:
                        this.tabNot.repaint();
                        break;
                }
            });

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

            let id = WUtil.getNumber(item, ISched.sID);
            if (!id) return;

            this.clearDet();

            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.enableDet(false);
            }

            jrpc.execute('SCHEDULAZIONI.read', [id], (result) => {
                result[ISched.sID_SERVIZIO] = getIdVal(result, ISched.sID_SERVIZIO);
                result[ISched.sID_ATTIVITA] = getIdVal(result, ISched.sID_ATTIVITA);

                this.fpDetail.setState(result);
                this.tabCon.setState(WUtil.getArray(result, ISched.sCONFIGURAZIONE));
                this.tabPar.setState(WUtil.getArray(result, ISched.sPARAMETRI));
                this.tabNot.setState(WUtil.getArray(result, ISched.sNOTIFICA));

                let cst = WUtil.getString(result, ISched.sSTATO);
                if(cst == 'D') {
                    this.btnToggle.setText('Abilita', WUX.WIcon.THUMBS_O_UP);
                    this.btnToggle.setText('Abilita', WUX.WIcon.THUMBS_O_UP);
                }
                else {
                    this.btnToggle.setText('Disabilita', WUX.WIcon.THUMBS_O_DOWN);
                    this.btnToggle.setText('Disabilita', WUX.WIcon.THUMBS_O_DOWN);
                }

                this.status = this.iSTATUS_VIEW;
            });
        }

        protected clearDet() {
            this.fpDetail.clear();
            this.tabCon.setState([]);
            this.tabPar.setState([]);
            this.tabNot.setState([]);
        }

        protected enableDet(e: boolean) {
            this.fpDetail.enabled = e;
            this.tabCon.enabled = e;
            this.tabPar.enabled = e;
            this.tabNot.enabled = e;
        }
    }
}