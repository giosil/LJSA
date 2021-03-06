namespace GUI {

    import WUtil = WUX.WUtil;

    export class GUIAttivita extends WUX.WComponent {
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
        protected dlgCon: DlgAttCon;
        protected dlgPar: DlgAttPar;
        protected dlgNot: DlgAttNot;
        // Stati
        protected isNew: boolean;
        protected status: number;
        readonly iSTATUS_STARTUP = 0;
        readonly iSTATUS_VIEW = 1;
        readonly iSTATUS_EDITING = 2;

        constructor(id?: string) {
            super(id ? id : '*', 'GUIAttivita');
            this.status = this.iSTATUS_STARTUP;

            this.dlgCon = new DlgAttCon(this.subId('dlgac'));
            this.dlgCon.onHiddenModal((e: JQueryEventObject) => {
                if (!this.dlgCon.ok) return;
                let d = this.tabCon.getState();
                let s = this.dlgCon.getState();
                let i = WUtil.indexOf(d, IAtt.sCONF_OPZIONE, s[IAtt.sCONF_OPZIONE]);
                if(i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                this.tabCon.setState(d);
            });
            this.dlgPar = new DlgAttPar(this.subId('dlgap'));
            this.dlgPar.onHiddenModal((e: JQueryEventObject) => {
                if (!this.dlgPar.ok) return;
                let d = this.tabPar.getState();
                let s = this.dlgPar.getState();
                let i = WUtil.indexOf(d, IAtt.sPAR_PARAMETRO, s[IAtt.sPAR_PARAMETRO]);
                if(i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                this.tabPar.setState(d);
            });
            this.dlgNot = new DlgAttNot(this.subId('dlgan'));
            this.dlgNot.onHiddenModal((e: JQueryEventObject) => {
                if (!this.dlgNot.ok) return;
                let d = this.tabNot.getState();
                let s = this.dlgNot.getState();
                let i = GUI.indexOf(d, IAtt.sNOT_EVENTO, IAtt.sNOT_DESTINAZIONE, s[IAtt.sCONF_OPZIONE] + ':' + s[IAtt.sNOT_DESTINAZIONE]);
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
                // A fronte di una ricerca esplicita si annulla l'elemento da selezionare
                this.selId = null;
                this.find();
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
            this.fpFilter.addComponent(IAtt.sID_SERVIZIO, 'Servizio', new LJSASelServizi());
            this.fpFilter.addTextField(IAtt.sID_ATTIVITA, 'Codice');
            this.fpFilter.addTextField(IAtt.sDESCRIZIONE, 'Descrizione');

            this.selSerDet = new LJSASelServizi();

            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addComponent(IAtt.sID_SERVIZIO, 'Servizio', this.selSerDet);
            this.fpDetail.addTextField(IAtt.sID_ATTIVITA, 'Codice');
            this.fpDetail.addRow();
            this.fpDetail.addComponent(IAtt.sCLASSE,      'Classe', new LJSASelClassi());
            this.fpDetail.addTextField(IAtt.sDESCRIZIONE, 'Descrizione');
            this.fpDetail.addInternalField(IAtt.sID_CREDENZIALE_INS);
            this.fpDetail.addInternalField(IAtt.sDATA_INS);
            this.fpDetail.addInternalField(IAtt.sORA_INS);
            this.fpDetail.enabled = false;

            this.fpDetail.setMandatory(IAtt.sID_SERVIZIO, IAtt.sID_ATTIVITA, IAtt.sCLASSE, IAtt.sDESCRIZIONE);

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
                // Set default job configuration
                this.setDefCon();
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
                this.fpDetail.setEnabled(IAtt.sID_SERVIZIO, false);
                this.fpDetail.setEnabled(IAtt.sID_ATTIVITA, false);

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
                values[IAtt.sCONFIGURAZIONE] = this.tabCon.getState();
                values[IAtt.sPARAMETRI] = this.tabPar.getState();
                values[IAtt.sNOTIFICA] = this.tabNot.getState();
                GUI.putUserLog(values);

                if (this.isNew) {
                    jrpc.execute('ATTIVITA.insert', [values], (result) => {
                        this.status = this.iSTATUS_VIEW;

                        this.enableDet(false);

                        this.selId = result[IAtt.sID_SERVIZIO] + ":" + result[IAtt.sID_ATTIVITA];
                        this.find();
                    });
                }
                else {
                    jrpc.execute('ATTIVITA.update', [values], (result) => {
                        this.status = this.iSTATUS_VIEW;

                        this.enableDet(false);

                        this.selId = result[IAtt.sID_SERVIZIO] + ":" + result[IAtt.sID_ATTIVITA];
                        let selRows = this.tabResult.getSelectedRows();
                        if (!selRows || !selRows.length) {
                            this.find();
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
                let ids = WUtil.getString(rd[0], IAtt.sID_SERVIZIO);
                let ida = WUtil.getString(rd[0], IAtt.sID_ATTIVITA);
                WUX.confirm(GUI.MSG.CONF_DELETE, (res: any) => {
                    if (!res) return;
                    jrpc.execute('ATTIVITA.delete', [ids, ida], (result) => {
                        this.find();
                    });
                });
            });

            let rc = [
                ['Servizio', IAtt.sID_SERVIZIO],
                ['Codice', IAtt.sID_ATTIVITA],
                ['Descrizione', IAtt.sDESCRIZIONE],
                ['Classe', IAtt.sCLASSE],
                ['Id Cred. Ins.', IAtt.sID_CREDENZIALE_INS],
                ['Data Ins.', IAtt.sDATA_INS],
                ['Ora Ins.', IAtt.sORA_INS],
                ['Id Cred. Agg.', IAtt.sID_CREDENZIALE_AGG],
                ['Data Agg.', IAtt.sDATA_AGG],
                ['Ora Agg.', IAtt.sORA_AGG]
            ];
            this.tabResult = new WUX.WDXTable(this.subId('tr'), WUtil.col(rc, 0), WUtil.col(rc, 1));
            this.tabResult.css({ h: 220 });
            this.tabResult.widths = [100];
            this.tabResult.onSelectionChanged((e: { element?: JQuery, selectedRowsData?: Array<any> }) => {

                this.onSelect();

            });

            this.tabCon = new WUX.WDXTable(this.subId('tbc'), ['Opzione', 'Descrizione', 'Valori', 'Predefinito'], [IAtt.sCONF_OPZIONE, IAtt.sCONF_DESCRIZIONE, IAtt.sCONF_VALORI, IAtt.sCONF_PREDEFINITO]);
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

            this.tabPar = new WUX.WDXTable(this.subId('tbp'), ['Parametro', 'Descrizione', 'Valori', 'Predefinito'], [IAtt.sPAR_PARAMETRO, IAtt.sPAR_DESCRIZIONE, IAtt.sPAR_VALORI, IAtt.sPAR_PREDEFINITO]);
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

            this.tabNot = new WUX.WDXTable(this.subId('tbn'), ['Evento', 'Destinazione'], [IAtt.sNOT_EVENTO, IAtt.sNOT_DESTINAZIONE]);
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
            // Left side
            this.cntActions.left.add(this.btnOpen);
            this.cntActions.left.add(this.btnDelete);
            this.cntActions.left.add(this.btnSave);
            this.cntActions.left.add(this.btnCancel);
            // Right side 
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

            let ids = WUtil.getString(item, IAtt.sID_SERVIZIO);
            let ida = WUtil.getString(item, IAtt.sID_ATTIVITA);
            if (!ids || !ida) return;

            this.clearDet();

            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.enableDet(false);
            }

            jrpc.execute('ATTIVITA.read', [ids, ida], (result) => {
                result[IAtt.sID_SERVIZIO] = getIdVal(result, IAtt.sID_SERVIZIO);

                this.fpDetail.setState(result);
                this.tabCon.setState(WUtil.getArray(result, IAtt.sCONFIGURAZIONE));
                this.tabPar.setState(WUtil.getArray(result, IAtt.sPARAMETRI));
                this.tabNot.setState(WUtil.getArray(result, IAtt.sNOTIFICA));

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

        protected find() {
            let user = GUI.getUserLogged();
            jrpc.execute('ATTIVITA.find', [this.fpFilter.getState(), user.groups], (result) => {
                this.tabResult.setState(result);
                this.clearDet();
                this.status = this.iSTATUS_STARTUP;
                if (this.selId) {
                    let idx = GUI.indexOf(result, IAtt.sID_SERVIZIO, IAtt.sID_ATTIVITA, this.selId);
                    if (idx >= 0) {
                        setTimeout(() => {
                            this.tabResult.select([idx]);
                        }, 100);
                    }
                    this.selId = null;
                }
            });
        }

        protected setDefCon() {
           let d = [
               ['nolog','Se S le elaborazioni NON vengono tracciate in archivio','S, N','N'],
               ['attachFiles','Se S i file prodotti vengono inviati in allegato alla mail','S, N','N'],
               ['attachErrorFiles','Se S i file di errore prodotti vengono inviati in allegato alla mail','S, N','N'],
               ['compressFiles','Se S i file prodotti vengono compressi','S, N','N'],
               ['excludeHolidays','Se S vengono esclusi i giorni festivi','S, N','N'],
               ['single','Se S si bloccano esecuzioni sovrapposte dello stesso job','S, N','N'],
               ['fileInfo','Se S viene creato il file di informazioni predefinito','S, N','N'],
               ['timeout','Timeout di elaborazione espresso in minuti','','0']
           ];
           let s = [];
           for(let i=0; i < d.length; i++) {
               let r = {};
               r[IAtt.sCONF_OPZIONE] = d[i][0];
               r[IAtt.sCONF_DESCRIZIONE] = d[i][1];
               r[IAtt.sCONF_VALORI] = d[i][2];
               r[IAtt.sCONF_PREDEFINITO] = d[i][3];
               s.push(r);
           }
           this.tabCon.setState(s);
        }
    }
}