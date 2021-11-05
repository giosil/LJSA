var GUI;
(function (GUI) {
    var WUtil = WUX.WUtil;
    var GUIAttivita = (function (_super) {
        __extends(GUIAttivita, _super);
        function GUIAttivita(id) {
            var _this = _super.call(this, id ? id : '*', 'GUIAttivita') || this;
            _this.iSTATUS_STARTUP = 0;
            _this.iSTATUS_VIEW = 1;
            _this.iSTATUS_EDITING = 2;
            _this.status = _this.iSTATUS_STARTUP;
            _this.dlgCon = new GUI.DlgAttCon(_this.subId('dlgac'));
            _this.dlgCon.onHiddenModal(function (e) {
                if (!_this.dlgCon.ok)
                    return;
                var d = _this.tabCon.getState();
                var s = _this.dlgCon.getState();
                var i = WUtil.indexOf(d, GUI.IAtt.sCONF_OPZIONE, s[GUI.IAtt.sCONF_OPZIONE]);
                if (i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                _this.tabCon.setState(d);
            });
            _this.dlgPar = new GUI.DlgAttPar(_this.subId('dlgap'));
            _this.dlgPar.onHiddenModal(function (e) {
                if (!_this.dlgPar.ok)
                    return;
                var d = _this.tabPar.getState();
                var s = _this.dlgPar.getState();
                var i = WUtil.indexOf(d, GUI.IAtt.sPAR_PARAMETRO, s[GUI.IAtt.sPAR_PARAMETRO]);
                if (i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                _this.tabPar.setState(d);
            });
            _this.dlgNot = new GUI.DlgAttNot(_this.subId('dlgan'));
            _this.dlgNot.onHiddenModal(function (e) {
                if (!_this.dlgNot.ok)
                    return;
                var d = _this.tabNot.getState();
                var s = _this.dlgNot.getState();
                var i = GUI.indexOf(d, GUI.IAtt.sNOT_EVENTO, GUI.IAtt.sNOT_DESTINAZIONE, s[GUI.IAtt.sCONF_OPZIONE] + ':' + s[GUI.IAtt.sNOT_DESTINAZIONE]);
                if (i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                _this.tabNot.setState(d);
            });
            return _this;
        }
        GUIAttivita.prototype.render = function () {
            var _this = this;
            this.btnFind = new WUX.WButton(this.subId('bf'), GUI.TXT.FIND, '', WUX.BTN.SM_PRIMARY);
            this.btnFind.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var check = _this.fpFilter.checkMandatory(true, true, false);
                if (check) {
                    WUX.showWarning('Specificare i seguenti campi: ' + check);
                    return;
                }
                var box = WUX.getComponent('boxFilter');
                if (box instanceof WUX.WBox) {
                    _this.tagsFilter.setState(_this.fpFilter.getValues(true));
                    box.collapse();
                }
                var user = GUI.getUserLogged();
                jrpc.execute('ATTIVITA.find', [_this.fpFilter.getState(), user.groups], function (result) {
                    _this.tabResult.setState(result);
                    _this.clearDet();
                    _this.status = _this.iSTATUS_STARTUP;
                    if (_this.selId) {
                        var idx_1 = GUI.indexOf(result, GUI.IAtt.sID_SERVIZIO, GUI.IAtt.sID_ATTIVITA, _this.selId);
                        if (idx_1 >= 0) {
                            setTimeout(function () {
                                _this.tabResult.select([idx_1]);
                            }, 100);
                        }
                        _this.selId = null;
                    }
                });
            });
            this.btnReset = new WUX.WButton(this.subId('br'), GUI.TXT.RESET, '', WUX.BTN.SM_SECONDARY);
            this.btnReset.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                _this.fpFilter.clear();
                _this.tagsFilter.setState({});
                _this.tabResult.setState([]);
                _this.clearDet();
                _this.status = _this.iSTATUS_STARTUP;
            });
            this.fpFilter = new WUX.WFormPanel(this.subId('ff'));
            this.fpFilter.addRow();
            this.fpFilter.addComponent(GUI.IAtt.sID_SERVIZIO, 'Servizio', new GUI.LJSASelServizi());
            this.fpFilter.addTextField(GUI.IAtt.sID_ATTIVITA, 'Codice');
            this.fpFilter.addTextField(GUI.IAtt.sDESCRIZIONE, 'Descrizione');
            this.selSerDet = new GUI.LJSASelServizi();
            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addComponent(GUI.IAtt.sID_SERVIZIO, 'Servizio', this.selSerDet);
            this.fpDetail.addTextField(GUI.IAtt.sID_ATTIVITA, 'Codice');
            this.fpDetail.addRow();
            this.fpDetail.addTextField(GUI.IAtt.sCLASSE, 'Classe');
            this.fpDetail.addTextField(GUI.IAtt.sDESCRIZIONE, 'Descrizione');
            this.fpDetail.addInternalField(GUI.IAtt.sID_CREDENZIALE_INS);
            this.fpDetail.addInternalField(GUI.IAtt.sDATA_INS);
            this.fpDetail.addInternalField(GUI.IAtt.sORA_INS);
            this.fpDetail.enabled = false;
            this.fpFilter.onEnterPressed(function (e) {
                _this.btnFind.trigger('click');
            });
            this.btnNew = new WUX.WButton(this.subId('bn'), GUI.TXT.NEW, '', WUX.BTN.SM_INFO);
            this.btnNew.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    _this.btnNew.blur();
                    return;
                }
                _this.isNew = true;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.tabResult.clearSelection();
                _this.enableDet(true);
                _this.fpDetail.clear();
                _this.selSerDet.setState(GUI._defService);
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnOpen = new WUX.WButton(this.subId('bo'), GUI.TXT.OPEN, GUI.ICO.OPEN, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnOpen.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    _this.btnOpen.blur();
                    return;
                }
                var sr = _this.tabResult.getSelectedRows();
                if (!sr || !sr.length) {
                    WUX.showWarning('Seleziona l\'elemento da modificare');
                    _this.btnOpen.blur();
                    return;
                }
                _this.isNew = false;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.enableDet(true);
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnSave = new WUX.WButton(this.subId('bs'), GUI.TXT.SAVE, GUI.ICO.SAVE, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnSave.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    WUX.showWarning('Cliccare su Modifica.');
                    _this.btnSave.blur();
                    return;
                }
                var check = _this.fpDetail.checkMandatory(true);
                if (check) {
                    _this.btnSave.blur();
                    WUX.showWarning('Specificare: ' + check);
                    return;
                }
                var values = _this.fpDetail.getState();
                values[GUI.IAtt.sCONFIGURAZIONE] = _this.tabCon.getState();
                values[GUI.IAtt.sPARAMETRI] = _this.tabPar.getState();
                values[GUI.IAtt.sNOTIFICA] = _this.tabNot.getState();
                GUI.putUserLog(values);
                if (_this.isNew) {
                    jrpc.execute('ATTIVITA.insert', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.enableDet(false);
                        _this.selId = result[GUI.IAtt.sID_SERVIZIO] + ":" + result[GUI.IAtt.sID_ATTIVITA];
                        _this.btnFind.trigger('click');
                    });
                }
                else {
                    jrpc.execute('ATTIVITA.update', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.enableDet(false);
                        _this.selId = result[GUI.IAtt.sID_SERVIZIO] + ":" + result[GUI.IAtt.sID_ATTIVITA];
                        var selRows = _this.tabResult.getSelectedRows();
                        if (!selRows || !selRows.length) {
                            _this.btnFind.trigger('click');
                        }
                        else {
                            var idx_2 = selRows[0];
                            var records = _this.tabResult.getState();
                            records[idx_2] = result;
                            _this.tabResult.refresh();
                            setTimeout(function () {
                                _this.tabResult.select([idx_2]);
                            }, 100);
                        }
                    });
                }
            });
            this.btnCancel = new WUX.WButton(this.subId('bc'), GUI.TXT.CANCEL, GUI.ICO.CANCEL, WUX.BTN.ACT_OUTLINE_INFO);
            this.btnCancel.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    _this.btnCancel.blur();
                    return;
                }
                WUX.confirm(GUI.MSG.CONF_CANCEL, function (res) {
                    if (!res)
                        return;
                    if (_this.isNew) {
                        _this.clearDet();
                    }
                    else {
                        _this.onSelect();
                    }
                    _this.status = _this.iSTATUS_VIEW;
                    _this.enableDet(false);
                    _this.selId = null;
                });
            });
            this.btnDelete = new WUX.WButton(this.subId('bd'), GUI.TXT.DELETE, GUI.ICO.DELETE, WUX.BTN.ACT_OUTLINE_DANGER);
            this.btnDelete.on('click', function (e) {
                _this.selId = null;
                _this.btnDelete.blur();
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var rd = _this.tabResult.getSelectedRowsData();
                if (!rd || !rd.length)
                    return;
                var ids = WUtil.getString(rd[0], GUI.IAtt.sID_SERVIZIO);
                var ida = WUtil.getString(rd[0], GUI.IAtt.sID_ATTIVITA);
                WUX.confirm(GUI.MSG.CONF_DELETE, function (res) {
                    if (!res)
                        return;
                    jrpc.execute('ATTIVITA.delete', [ids, ida], function (result) {
                        _this.btnFind.trigger('click');
                    });
                });
            });
            var rc = [
                ['Servizio', GUI.IAtt.sID_SERVIZIO],
                ['Codice', GUI.IAtt.sID_ATTIVITA],
                ['Descrizione', GUI.IAtt.sDESCRIZIONE],
                ['Classe', GUI.IAtt.sCLASSE],
                ['Id Cred. Ins.', GUI.IAtt.sID_CREDENZIALE_INS],
                ['Data Ins.', GUI.IAtt.sDATA_INS],
                ['Ora Ins.', GUI.IAtt.sORA_INS],
                ['Id Cred. Agg.', GUI.IAtt.sID_CREDENZIALE_AGG],
                ['Data Agg.', GUI.IAtt.sDATA_AGG],
                ['Ora Agg.', GUI.IAtt.sORA_AGG]
            ];
            this.tabResult = new WUX.WDXTable(this.subId('tr'), WUtil.col(rc, 0), WUtil.col(rc, 1));
            this.tabResult.css({ h: 220 });
            this.tabResult.widths = [100];
            this.tabResult.onSelectionChanged(function (e) {
                _this.onSelect();
            });
            this.tabCon = new WUX.WDXTable(this.subId('tbc'), ['Opzione', 'Descrizione', 'Valori', 'Predefinito'], [GUI.IAtt.sCONF_OPZIONE, GUI.IAtt.sCONF_DESCRIZIONE, GUI.IAtt.sCONF_VALORI, GUI.IAtt.sCONF_PREDEFINITO]);
            this.tabCon.selectionMode = 'single';
            this.tabCon.css({ h: 240 });
            this.tabCon.onDoubleClick(function (e) {
                var s = _this.tabCon.getSelectedRowsData();
                if (!s || !s.length)
                    return;
                _this.dlgCon.setState(s[0]);
                _this.dlgCon.show(_this);
            });
            this.btnAddCon = new WUX.WButton(this.subId('bac'), GUI.TXT.ADD, '', WUX.BTN.SM_PRIMARY);
            this.btnAddCon.on('click', function (e) {
                _this.dlgCon.setState(null);
                _this.dlgCon.show(_this);
            });
            this.btnRemCon = new WUX.WButton(this.subId('brc'), GUI.TXT.REMOVE, '', WUX.BTN.SM_DANGER);
            this.btnRemCon.on('click', function (e) {
                var s = _this.tabCon.getSelectedRows();
                if (!s || !s.length)
                    return;
                var d = _this.tabCon.getState();
                d.splice(s[0], 1);
                _this.tabCon.setState(d);
            });
            this.tabPar = new WUX.WDXTable(this.subId('tbp'), ['Parametro', 'Descrizione', 'Valori', 'Predefinito'], [GUI.IAtt.sPAR_PARAMETRO, GUI.IAtt.sPAR_DESCRIZIONE, GUI.IAtt.sPAR_VALORI, GUI.IAtt.sPAR_PREDEFINITO]);
            this.tabPar.selectionMode = 'single';
            this.tabPar.css({ h: 240 });
            this.tabPar.onDoubleClick(function (e) {
                var s = _this.tabPar.getSelectedRowsData();
                if (!s || !s.length)
                    return;
                _this.dlgPar.setState(s[0]);
                _this.dlgPar.show(_this);
            });
            this.btnAddPar = new WUX.WButton(this.subId('bap'), GUI.TXT.ADD, '', WUX.BTN.SM_PRIMARY);
            this.btnAddPar.on('click', function (e) {
                _this.dlgPar.setState(null);
                _this.dlgPar.show(_this);
            });
            this.btnRemPar = new WUX.WButton(this.subId('brp'), GUI.TXT.REMOVE, '', WUX.BTN.SM_DANGER);
            this.btnRemPar.on('click', function (e) {
                var s = _this.tabPar.getSelectedRows();
                if (!s || !s.length)
                    return;
                var d = _this.tabPar.getState();
                d.splice(s[0], 1);
                _this.tabPar.setState(d);
            });
            this.tabNot = new WUX.WDXTable(this.subId('tbn'), ['Evento', 'Destinazione'], [GUI.IAtt.sNOT_EVENTO, GUI.IAtt.sNOT_DESTINAZIONE]);
            this.tabNot.selectionMode = 'single';
            this.tabNot.css({ h: 240 });
            this.tabNot.onDoubleClick(function (e) {
                var s = _this.tabPar.getSelectedRowsData();
                if (!s || !s.length)
                    return;
                _this.dlgNot.setState(s[0]);
                _this.dlgNot.show(_this);
            });
            this.btnAddNot = new WUX.WButton(this.subId('ban'), GUI.TXT.ADD, '', WUX.BTN.SM_PRIMARY);
            this.btnAddNot.on('click', function (e) {
                _this.dlgNot.setState(null);
                _this.dlgNot.show(_this);
            });
            this.btnRemNot = new WUX.WButton(this.subId('brn'), GUI.TXT.REMOVE, '', WUX.BTN.SM_DANGER);
            this.btnRemNot.on('click', function (e) {
                var s = _this.tabNot.getSelectedRows();
                if (!s || !s.length)
                    return;
                var d = _this.tabNot.getState();
                d.splice(s[0], 1);
                _this.tabNot.setState(d);
            });
            this.cntActions = new GUI.AppTableActions('ta');
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
            this.tcoDetail.on('statechange', function (e) {
                var itab = _this.tcoDetail.getState();
                switch (itab) {
                    case 0:
                        break;
                    case 1:
                        _this.tabCon.repaint();
                        break;
                    case 2:
                        _this.tabPar.repaint();
                        break;
                    case 3:
                        _this.tabNot.repaint();
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
                .end()
                .addBox()
                .addGroup({ classStyle: 'search-actions-and-results-wrapper' }, this.cntActions, this.tabResult)
                .end()
                .addRow()
                .addCol('12').section('Dettaglio')
                .add(this.tcoDetail);
            return this.container;
        };
        GUIAttivita.prototype.collapseHandler = function (e) {
            var c = WUtil.getBoolean(e.data, 'collapsed');
            if (c) {
                this.tagsFilter.setState({});
            }
            else {
                this.tagsFilter.setState(this.fpFilter.getValues(true));
            }
        };
        GUIAttivita.prototype.onSelect = function () {
            var _this = this;
            var item = WUtil.getItem(this.tabResult.getSelectedRowsData(), 0);
            if (!item)
                return;
            var ids = WUtil.getString(item, GUI.IAtt.sID_SERVIZIO);
            var ida = WUtil.getString(item, GUI.IAtt.sID_ATTIVITA);
            if (!ids || !ida)
                return;
            this.clearDet();
            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.enableDet(false);
            }
            jrpc.execute('ATTIVITA.read', [ids, ida], function (result) {
                result[GUI.IAtt.sID_SERVIZIO] = GUI.getIdVal(result, GUI.IAtt.sID_SERVIZIO);
                _this.fpDetail.setState(result);
                _this.tabCon.setState(WUtil.getArray(result, GUI.IAtt.sCONFIGURAZIONE));
                _this.tabPar.setState(WUtil.getArray(result, GUI.IAtt.sPARAMETRI));
                _this.tabNot.setState(WUtil.getArray(result, GUI.IAtt.sNOTIFICA));
                _this.status = _this.iSTATUS_VIEW;
            });
        };
        GUIAttivita.prototype.clearDet = function () {
            this.fpDetail.clear();
            this.tabCon.setState([]);
            this.tabPar.setState([]);
            this.tabNot.setState([]);
        };
        GUIAttivita.prototype.enableDet = function (e) {
            this.fpDetail.enabled = e;
            this.tabCon.enabled = e;
            this.tabPar.enabled = e;
            this.tabNot.enabled = e;
        };
        return GUIAttivita;
    }(WUX.WComponent));
    GUI.GUIAttivita = GUIAttivita;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var WUtil = WUX.WUtil;
    GUI.LJSA_SERVICE = 'LJSA';
    GUI._defService = '';
    function getUserLogged() {
        var userLogged = window ? window['_userLogged'] : undefined;
        if (userLogged && typeof userLogged == 'object')
            return userLogged;
        return { id: 1, userName: 'dew', currLogin: new Date(), role: 'admin', groups: [GUI.LJSA_SERVICE], structures: [], email: 'test@dew.org', mobile: '3491234567', tokenId: 'KURJPghMTJ' };
    }
    GUI.getUserLogged = getUserLogged;
    function getConfig() {
        var config = window ? window['_config'] : undefined;
        if (config && typeof config == 'object')
            return config;
        return {};
    }
    GUI.getConfig = getConfig;
    function getLocale() {
        var u = getUserLogged();
        if (u != null && u.locale)
            return u.locale;
        return WUX.WUtil.getString(getConfig(), 'locale', '');
    }
    GUI.getLocale = getLocale;
    function isDevMode() {
        var userLogged = window ? window['_userLogged'] : undefined;
        if (userLogged && typeof userLogged == 'object')
            return false;
        return true;
    }
    GUI.isDevMode = isDevMode;
    function indexOf(a, k1, k2, v) {
        if (!a || !k1 || !k2)
            return -1;
        var y = WUtil.toArray(a);
        for (var i = 0; i < y.length; i++) {
            var w = WUtil.getValue(y[i], k1) + ':' + WUtil.getValue(y[i], k2);
            if (w == v)
                return i;
        }
        return -1;
    }
    GUI.indexOf = indexOf;
    function putUserLog(a) {
        if (!a)
            return a;
        a[ICommon.sUSER_LOG] = getUserLogged().userName;
        return a;
    }
    GUI.putUserLog = putUserLog;
    function getIdVal(a, k) {
        if (!a || !k)
            return a;
        var v = a[k];
        if (Array.isArray(v)) {
            if (v.length > 1) {
                return v[0];
            }
            else {
                return null;
            }
        }
        return v;
    }
    GUI.getIdVal = getIdVal;
    var ICommon = (function () {
        function ICommon() {
        }
        ICommon.sUSER_LOG = '#u';
        return ICommon;
    }());
    GUI.ICommon = ICommon;
    var IServizio = (function () {
        function IServizio() {
        }
        IServizio.sID_SERVIZIO = 'id_servizio';
        IServizio.sDESCRIZIONE = 'descrizione';
        IServizio.sATTIVO = 'attivo';
        IServizio.sCREDENZIALI = 'credenziali';
        return IServizio;
    }());
    GUI.IServizio = IServizio;
    var IClasse = (function () {
        function IClasse() {
        }
        IClasse.sCLASSE = "classe";
        IClasse.sDESCRIZIONE = "descrizione";
        IClasse.sATTIVITA = "attivita";
        return IClasse;
    }());
    GUI.IClasse = IClasse;
    var ICredenziale = (function () {
        function ICredenziale() {
        }
        ICredenziale.sID_SERVIZIO = "id_servizio";
        ICredenziale.sID_CREDENZIALE = "id_credenziale";
        ICredenziale.sCREDENZIALI = "credenziali";
        ICredenziale.sEMAIL = "email";
        ICredenziale.sATTIVO = "attivo";
        return ICredenziale;
    }());
    GUI.ICredenziale = ICredenziale;
    var IAtt = (function () {
        function IAtt() {
        }
        IAtt.sID_SERVIZIO = "id_servizio";
        IAtt.sID_ATTIVITA = "id_attivita";
        IAtt.sDESCRIZIONE = "descrizione";
        IAtt.sCLASSE = "classe";
        IAtt.sATTIVO = "attivo";
        IAtt.sID_CREDENZIALE_INS = "id_credenziale_ins";
        IAtt.sDATA_INS = "data_inserimento";
        IAtt.sORA_INS = "ora_inserimento";
        IAtt.sID_CREDENZIALE_AGG = "id_credenziale_agg";
        IAtt.sDATA_AGG = "data_aggiornamento";
        IAtt.sORA_AGG = "ora_aggiornamento";
        IAtt.sCONFIGURAZIONE = "configurazione";
        IAtt.sCONF_OPZIONE = "opzione";
        IAtt.sCONF_DESCRIZIONE = "descrizione";
        IAtt.sCONF_VALORI = "valori";
        IAtt.sCONF_PREDEFINITO = "predefinito";
        IAtt.sPARAMETRI = "parametri";
        IAtt.sPAR_PARAMETRO = "parametro";
        IAtt.sPAR_DESCRIZIONE = "descrizione";
        IAtt.sPAR_VALORI = "valori";
        IAtt.sPAR_PREDEFINITO = "predefinito";
        IAtt.sNOTIFICA = "notifica";
        IAtt.sNOT_EVENTO = "evento";
        IAtt.sNOT_DESTINAZIONE = "destinazione";
        return IAtt;
    }());
    GUI.IAtt = IAtt;
    var ISched = (function () {
        function ISched() {
        }
        ISched.sID = "id_schedulazione";
        ISched.sID_SERVIZIO = "id_servizio";
        ISched.sID_ATTIVITA = "id_attivita";
        ISched.sSCHEDULAZIONE = "schedulazione";
        ISched.sDESCRIZIONE = "descrizione";
        ISched.sID_CREDENZIALE_INS = "id_credenziale_ins";
        ISched.sDATA_INS = "data_inserimento";
        ISched.sORA_INS = "ora_inserimento";
        ISched.sID_CREDENZIALE_AGG = "id_credenziale_agg";
        ISched.sDATA_AGG = "data_aggiornamento";
        ISched.sORA_AGG = "ora_aggiornamento";
        ISched.sSTATO = "stato";
        ISched.sINIZIO_VALIDITA = "inizio_validita";
        ISched.sFINE_VALIDITA = "fine_validita";
        ISched.sDATA_SCHED = "data_schedulazione";
        ISched.sORA_SCHED = "ora_schedulazione";
        ISched.sATTIVO = "a";
        ISched.sPARAMETRI = "parametri";
        ISched.sPAR_PARAMETRO = "parametro";
        ISched.sPAR_VALORE = "valore";
        ISched.sPAR_DA_ATTIVITA = "da_attivita";
        ISched.sPAR_OVERWRITE = "overwrite";
        ISched.sPAR_DESCRIZIONE = IAtt.sPAR_DESCRIZIONE;
        ISched.sPAR_VALORI = IAtt.sPAR_VALORI;
        ISched.sPAR_PREDEFINITO = IAtt.sPAR_PREDEFINITO;
        ISched.sCONFIGURAZIONE = "configurazione";
        ISched.sCONF_OPZIONE = "opzione";
        ISched.sCONF_VALORE = "valore";
        ISched.sCONF_DA_ATTIVITA = "da_attivita";
        ISched.sCONF_OVERWRITE = "overwrite";
        ISched.sCONF_DESCRIZIONE = IAtt.sCONF_DESCRIZIONE;
        ISched.sCONF_VALORI = IAtt.sCONF_VALORI;
        ISched.sCONF_PREDEFINITO = IAtt.sCONF_PREDEFINITO;
        ISched.sCONF_TIMEOUT = "timeout";
        ISched.sCONF_STOP_ON_TIMEOUT = "stopOnTimeout";
        ISched.sCONF_COMPRESS_FILES = "compressFiles";
        ISched.sCONF_ATTACH_FILES = "attachFiles";
        ISched.sCONF_ATTACH_ERR_FILES = "attachErrorFiles";
        ISched.sCONF_FILE_INFO = "fileInfo";
        ISched.sCONF_NO_LOG = "nolog";
        ISched.sNOTIFICA = "notifica";
        ISched.sNOT_EVENTO = "evento";
        ISched.sNOT_DESTINAZIONE = "destinazione";
        ISched.sNOT_DA_ATTIVITA = "da_attivita";
        ISched.sNOT_CANCELLATA = "cancellata";
        ISched.sESEC_COMPLETATE = "esecuzioni_completate";
        ISched.sESEC_INTERROTTE = "esecuzioni_interrotte";
        return ISched;
    }());
    GUI.ISched = ISched;
})(GUI || (GUI = {}));
WUX.global.locale = GUI.getLocale();
var jrpc = new JRPC("/LJSA/rpc");
jrpc.setUserName(GUI.getUserLogged().userName);
jrpc.setPassword(GUI.getUserLogged().tokenId);
var GUI;
(function (GUI) {
    var WUtil = WUX.WUtil;
    var GUIClassi = (function (_super) {
        __extends(GUIClassi, _super);
        function GUIClassi(id) {
            var _this = _super.call(this, id ? id : '*', 'GUIClassi') || this;
            _this.iSTATUS_STARTUP = 0;
            _this.iSTATUS_VIEW = 1;
            _this.iSTATUS_EDITING = 2;
            _this.status = _this.iSTATUS_STARTUP;
            return _this;
        }
        GUIClassi.prototype.render = function () {
            var _this = this;
            this.btnFind = new WUX.WButton(this.subId('bf'), GUI.TXT.FIND, '', WUX.BTN.SM_PRIMARY);
            this.btnFind.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var check = _this.fpFilter.checkMandatory(true, true, false);
                if (check) {
                    WUX.showWarning('Specificare i seguenti campi: ' + check);
                    return;
                }
                var box = WUX.getComponent('boxFilter');
                if (box instanceof WUX.WBox) {
                    _this.tagsFilter.setState(_this.fpFilter.getValues(true));
                    box.collapse();
                }
                jrpc.execute('CLASSI.find', [_this.fpFilter.getState()], function (result) {
                    _this.tabResult.setState(result);
                    _this.fpDetail.clear();
                    _this.status = _this.iSTATUS_STARTUP;
                    if (_this.selId) {
                        var idx_3 = WUtil.indexOf(result, GUI.IClasse.sCLASSE, _this.selId);
                        if (idx_3 >= 0) {
                            setTimeout(function () {
                                _this.tabResult.select([idx_3]);
                            }, 100);
                        }
                        _this.selId = null;
                    }
                });
            });
            this.btnReset = new WUX.WButton(this.subId('br'), GUI.TXT.RESET, '', WUX.BTN.SM_SECONDARY);
            this.btnReset.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                _this.fpFilter.clear();
                _this.tagsFilter.setState({});
                _this.tabResult.setState([]);
                _this.fpDetail.clear();
                _this.status = _this.iSTATUS_STARTUP;
            });
            this.fpFilter = new WUX.WFormPanel(this.subId('ff'));
            this.fpFilter.addRow();
            this.fpFilter.addTextField(GUI.IClasse.sCLASSE, 'Classe');
            this.fpFilter.addTextField(GUI.IClasse.sDESCRIZIONE, 'Descrizione');
            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addTextField(GUI.IClasse.sCLASSE, 'Classe');
            this.fpDetail.addTextField(GUI.IClasse.sDESCRIZIONE, 'Descrizione');
            this.fpDetail.enabled = false;
            this.fpFilter.onEnterPressed(function (e) {
                _this.btnFind.trigger('click');
            });
            this.btnNew = new WUX.WButton(this.subId('bn'), GUI.TXT.NEW, '', WUX.BTN.SM_INFO);
            this.btnNew.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    _this.btnNew.blur();
                    return;
                }
                _this.isNew = true;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.tabResult.clearSelection();
                _this.fpDetail.enabled = true;
                _this.fpDetail.clear();
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnOpen = new WUX.WButton(this.subId('bo'), GUI.TXT.OPEN, GUI.ICO.OPEN, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnOpen.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    _this.btnOpen.blur();
                    return;
                }
                var sr = _this.tabResult.getSelectedRows();
                if (!sr || !sr.length) {
                    WUX.showWarning('Seleziona l\'elemento da modificare');
                    _this.btnOpen.blur();
                    return;
                }
                _this.isNew = false;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.fpDetail.enabled = true;
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnSave = new WUX.WButton(this.subId('bs'), GUI.TXT.SAVE, GUI.ICO.SAVE, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnSave.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    WUX.showWarning('Cliccare su Modifica.');
                    _this.btnSave.blur();
                    return;
                }
                var check = _this.fpDetail.checkMandatory(true);
                if (check) {
                    _this.btnSave.blur();
                    WUX.showWarning('Specificare: ' + check);
                    return;
                }
                var values = _this.fpDetail.getState();
                if (_this.isNew) {
                    jrpc.execute('CLASSI.insert', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.IClasse.sCLASSE];
                        _this.btnFind.trigger('click');
                    });
                }
                else {
                    jrpc.execute('CLASSI.update', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.IClasse.sCLASSE];
                        var selRows = _this.tabResult.getSelectedRows();
                        if (!selRows || !selRows.length) {
                            _this.btnFind.trigger('click');
                        }
                        else {
                            var idx_4 = selRows[0];
                            var records = _this.tabResult.getState();
                            records[idx_4] = result;
                            _this.tabResult.refresh();
                            setTimeout(function () {
                                _this.tabResult.select([idx_4]);
                            }, 100);
                        }
                    });
                }
            });
            this.btnCancel = new WUX.WButton(this.subId('bc'), GUI.TXT.CANCEL, GUI.ICO.CANCEL, WUX.BTN.ACT_OUTLINE_INFO);
            this.btnCancel.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    _this.btnCancel.blur();
                    return;
                }
                WUX.confirm(GUI.MSG.CONF_CANCEL, function (res) {
                    if (!res)
                        return;
                    if (_this.isNew) {
                        _this.fpDetail.clear();
                    }
                    else {
                        _this.onSelect();
                    }
                    _this.status = _this.iSTATUS_VIEW;
                    _this.fpDetail.enabled = false;
                    _this.selId = null;
                });
            });
            this.btnDelete = new WUX.WButton(this.subId('bd'), GUI.TXT.DELETE, GUI.ICO.DELETE, WUX.BTN.ACT_OUTLINE_DANGER);
            this.btnDelete.on('click', function (e) {
                _this.selId = null;
                _this.btnDelete.blur();
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var rd = _this.tabResult.getSelectedRowsData();
                if (!rd || !rd.length)
                    return;
                var id = WUtil.getString(rd[0], GUI.IClasse.sCLASSE);
                WUX.confirm(GUI.MSG.CONF_DELETE, function (res) {
                    if (!res)
                        return;
                    jrpc.execute('CLASSI.delete', [id], function (result) {
                        _this.btnFind.trigger('click');
                    });
                });
            });
            var rc = [
                ['Classe', GUI.IClasse.sCLASSE],
                ['Descrizione', GUI.IClasse.sDESCRIZIONE]
            ];
            this.tabResult = new WUX.WDXTable(this.subId('tr'), WUtil.col(rc, 0), WUtil.col(rc, 1));
            this.tabResult.css({ h: 220 });
            this.tabResult.widths = [100];
            this.tabResult.onSelectionChanged(function (e) {
                _this.onSelect();
            });
            this.cntActions = new GUI.AppTableActions('ta');
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
                .end()
                .addBox()
                .addGroup({ classStyle: 'search-actions-and-results-wrapper' }, this.cntActions, this.tabResult)
                .end()
                .addRow()
                .addCol('12').section('Dettaglio')
                .add(this.tcoDetail);
            return this.container;
        };
        GUIClassi.prototype.collapseHandler = function (e) {
            var c = WUtil.getBoolean(e.data, 'collapsed');
            if (c) {
                this.tagsFilter.setState({});
            }
            else {
                this.tagsFilter.setState(this.fpFilter.getValues(true));
            }
        };
        GUIClassi.prototype.onSelect = function () {
            var _this = this;
            var item = WUtil.getItem(this.tabResult.getSelectedRowsData(), 0);
            if (!item)
                return;
            var id = WUtil.getString(item, GUI.IClasse.sCLASSE);
            if (!id)
                return;
            this.fpDetail.clear();
            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.fpDetail.enabled = false;
            }
            jrpc.execute('CLASSI.read', [id], function (result) {
                _this.fpDetail.setState(result);
                _this.status = _this.iSTATUS_VIEW;
            });
        };
        return GUIClassi;
    }(WUX.WComponent));
    GUI.GUIClassi = GUIClassi;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var WUtil = WUX.WUtil;
    var AppTableActions = (function (_super) {
        __extends(AppTableActions, _super);
        function AppTableActions(id) {
            var _this = _super.call(this, id, 'AppTableActions', null, 'table-actions-wrapper') || this;
            _this.left = new WUX.WContainer(_this.subId('l'), 'left-actions');
            _this.right = new WUX.WContainer(_this.subId('r'), 'right-actions');
            return _this;
        }
        AppTableActions.prototype.componentDidMount = function () {
            var $i = $('<div class="table-actions clearfix"></div>');
            this.root.append($i);
            this.left.mount($i);
            this.right.mount($i);
        };
        AppTableActions.prototype.setLeftVisible = function (v) {
            this.left.visible = v;
        };
        AppTableActions.prototype.setRightVisible = function (v) {
            this.right.visible = v;
        };
        return AppTableActions;
    }(WUX.WComponent));
    GUI.AppTableActions = AppTableActions;
    var LJSASelServizi = (function (_super) {
        __extends(LJSASelServizi, _super);
        function LJSASelServizi(id, multiple) {
            var _this = _super.call(this, id) || this;
            _this.multiple = multiple;
            _this.name = 'LJSASelServizi';
            return _this;
        }
        LJSASelServizi.prototype.updateState = function (nextState) {
            _super.prototype.updateState.call(this, nextState);
            var s = WUtil.toString(nextState);
            if (s)
                GUI._defService = s;
        };
        LJSASelServizi.prototype.componentDidMount = function () {
            var _this = this;
            var user = GUI.getUserLogged();
            jrpc.execute('SERVIZI.lookup', [user.groups], function (result) {
                var data = [];
                for (var i = 0; i < result.length; i++) {
                    var r = result[i];
                    var d = { id: r[0], text: r[0] };
                    data.push(d);
                }
                var options = {
                    data: data,
                    placeholder: "",
                    allowClear: true,
                };
                _this.init(options);
            });
        };
        return LJSASelServizi;
    }(WUX.WSelect2));
    GUI.LJSASelServizi = LJSASelServizi;
    var LJSASelClassi = (function (_super) {
        __extends(LJSASelClassi, _super);
        function LJSASelClassi(id, multiple) {
            var _this = _super.call(this, id, [], multiple) || this;
            _this.name = 'LJSASelClassi';
            return _this;
        }
        LJSASelClassi.prototype.componentDidMount = function () {
            var options = {
                ajax: {
                    dataType: "json",
                    delay: 400,
                    processResults: function (result, params) {
                        return {
                            results: result
                        };
                    },
                    transport: function (params, success, failure) {
                        jrpc.execute("CLASSI.lookup", [params.data.q], success);
                        return undefined;
                    }
                },
                placeholder: "",
                allowClear: true,
                minimumInputLength: 3
            };
            this.init(options);
        };
        return LJSASelClassi;
    }(WUX.WSelect2));
    GUI.LJSASelClassi = LJSASelClassi;
    var LJSASelAttivita = (function (_super) {
        __extends(LJSASelAttivita, _super);
        function LJSASelAttivita(id, multiple) {
            var _this = _super.call(this, id) || this;
            _this.multiple = multiple;
            _this.name = 'LJSASelAttivita';
            return _this;
        }
        Object.defineProperty(LJSASelAttivita.prototype, "service", {
            set: function (s) {
                this._serviceS = s;
                if (!s)
                    return;
                if (s == this._serviceL)
                    return;
                GUI._defService = s;
                this.reload(false);
            },
            enumerable: false,
            configurable: true
        });
        LJSASelAttivita.prototype.updateState = function (nextState) {
            _super.prototype.updateState.call(this, nextState);
            var s = WUtil.toString(nextState);
            if (s)
                GUI._defService = s;
        };
        LJSASelAttivita.prototype.componentDidMount = function () {
            var _this = this;
            jrpc.execute('ATTIVITA.lookup', [GUI._defService, ''], function (result) {
                _this._serviceL = GUI._defService;
                var data = [];
                for (var i = 0; i < result.length; i++) {
                    var r = result[i];
                    var d = { id: r[0], text: r[0] };
                    data.push(d);
                }
                var options = {
                    data: data,
                    placeholder: "",
                    allowClear: true,
                };
                _this.init(options);
            });
        };
        return LJSASelAttivita;
    }(WUX.WSelect2));
    GUI.LJSASelAttivita = LJSASelAttivita;
    var LJSASelStati = (function (_super) {
        __extends(LJSASelStati, _super);
        function LJSASelStati(id, multiple) {
            var _this = _super.call(this, id) || this;
            _this.multiple = multiple;
            _this.name = 'LJSASelStati';
            _this.options = [
                { id: '', text: '' },
                { id: 'A', text: '(A) Attiva' },
                { id: 'D', text: '(D) Disattivata' },
                { id: 'E', text: '(E) In Esecuzione' },
            ];
            return _this;
        }
        return LJSASelStati;
    }(WUX.WSelect2));
    GUI.LJSASelStati = LJSASelStati;
    var LJSASelEventi = (function (_super) {
        __extends(LJSASelEventi, _super);
        function LJSASelEventi(id, multiple) {
            var _this = _super.call(this, id) || this;
            _this.multiple = multiple;
            _this.name = 'LJSASelEventi';
            _this.options = [
                { id: '', text: '' },
                { id: 'R', text: '(R) Risultato elaborazione' },
                { id: 'E', text: '(E) Eccezione verificatasi' },
                { id: 'T', text: '(T) Timeout raggiunto' },
            ];
            return _this;
        }
        return LJSASelEventi;
    }(WUX.WSelect2));
    GUI.LJSASelEventi = LJSASelEventi;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var WUtil = WUX.WUtil;
    var GUICredenziali = (function (_super) {
        __extends(GUICredenziali, _super);
        function GUICredenziali(id) {
            var _this = _super.call(this, id ? id : '*', 'GUICredenziali') || this;
            _this.iSTATUS_STARTUP = 0;
            _this.iSTATUS_VIEW = 1;
            _this.iSTATUS_EDITING = 2;
            _this.status = _this.iSTATUS_STARTUP;
            return _this;
        }
        GUICredenziali.prototype.render = function () {
            var _this = this;
            this.btnFind = new WUX.WButton(this.subId('bf'), GUI.TXT.FIND, '', WUX.BTN.SM_PRIMARY);
            this.btnFind.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var check = _this.fpFilter.checkMandatory(true, true, false);
                if (check) {
                    WUX.showWarning('Specificare i seguenti campi: ' + check);
                    return;
                }
                var box = WUX.getComponent('boxFilter');
                if (box instanceof WUX.WBox) {
                    _this.tagsFilter.setState(_this.fpFilter.getValues(true));
                    box.collapse();
                }
                var user = GUI.getUserLogged();
                jrpc.execute('CREDENZIALI.find', [_this.fpFilter.getState(), user.groups], function (result) {
                    _this.tabResult.setState(result);
                    _this.fpDetail.clear();
                    _this.status = _this.iSTATUS_STARTUP;
                    if (_this.selId) {
                        var idx_5 = GUI.indexOf(result, GUI.ICredenziale.sID_SERVIZIO, GUI.ICredenziale.sID_CREDENZIALE, _this.selId);
                        if (idx_5 >= 0) {
                            setTimeout(function () {
                                _this.tabResult.select([idx_5]);
                            }, 100);
                        }
                        _this.selId = null;
                    }
                });
            });
            this.btnReset = new WUX.WButton(this.subId('br'), GUI.TXT.RESET, '', WUX.BTN.SM_SECONDARY);
            this.btnReset.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                _this.fpFilter.clear();
                _this.tagsFilter.setState({});
                _this.tabResult.setState([]);
                _this.fpDetail.clear();
                _this.status = _this.iSTATUS_STARTUP;
            });
            this.fpFilter = new WUX.WFormPanel(this.subId('ff'));
            this.fpFilter.addRow();
            this.fpFilter.addComponent(GUI.ICredenziale.sID_SERVIZIO, 'Servizio', new GUI.LJSASelServizi());
            this.fpFilter.addTextField(GUI.ICredenziale.sID_CREDENZIALE, 'Credenziale');
            this.fpFilter.addTextField(GUI.ICredenziale.sEMAIL, 'Email');
            this.selSerDet = new GUI.LJSASelServizi();
            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addComponent(GUI.ICredenziale.sID_SERVIZIO, 'Servizio', this.selSerDet);
            this.fpDetail.addTextField(GUI.ICredenziale.sID_CREDENZIALE, 'Credenziale');
            this.fpDetail.addRow();
            this.fpDetail.addPasswordField(GUI.ICredenziale.sCREDENZIALI, 'Password');
            this.fpDetail.addTextField(GUI.ICredenziale.sEMAIL, 'Email');
            this.fpDetail.enabled = false;
            this.fpFilter.onEnterPressed(function (e) {
                _this.btnFind.trigger('click');
            });
            this.btnNew = new WUX.WButton(this.subId('bn'), GUI.TXT.NEW, '', WUX.BTN.SM_INFO);
            this.btnNew.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    _this.btnNew.blur();
                    return;
                }
                _this.isNew = true;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.tabResult.clearSelection();
                _this.fpDetail.enabled = true;
                _this.fpDetail.clear();
                _this.selSerDet.setState(GUI._defService);
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnOpen = new WUX.WButton(this.subId('bo'), GUI.TXT.OPEN, GUI.ICO.OPEN, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnOpen.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    _this.btnOpen.blur();
                    return;
                }
                var sr = _this.tabResult.getSelectedRows();
                if (!sr || !sr.length) {
                    WUX.showWarning('Seleziona l\'elemento da modificare');
                    _this.btnOpen.blur();
                    return;
                }
                _this.isNew = false;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.fpDetail.enabled = true;
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnSave = new WUX.WButton(this.subId('bs'), GUI.TXT.SAVE, GUI.ICO.SAVE, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnSave.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    WUX.showWarning('Cliccare su Modifica.');
                    _this.btnSave.blur();
                    return;
                }
                var check = _this.fpDetail.checkMandatory(true);
                if (check) {
                    _this.btnSave.blur();
                    WUX.showWarning('Specificare: ' + check);
                    return;
                }
                var values = _this.fpDetail.getState();
                if (_this.isNew) {
                    jrpc.execute('CREDENZIALI.insert', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.ICredenziale.sID_SERVIZIO] + ":" + result[GUI.ICredenziale.sID_CREDENZIALE];
                        _this.btnFind.trigger('click');
                    });
                }
                else {
                    jrpc.execute('CREDENZIALI.update', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.ICredenziale.sID_SERVIZIO] + ":" + result[GUI.ICredenziale.sID_CREDENZIALE];
                        var selRows = _this.tabResult.getSelectedRows();
                        if (!selRows || !selRows.length) {
                            _this.btnFind.trigger('click');
                        }
                        else {
                            var idx_6 = selRows[0];
                            var records = _this.tabResult.getState();
                            records[idx_6] = result;
                            _this.tabResult.refresh();
                            setTimeout(function () {
                                _this.tabResult.select([idx_6]);
                            }, 100);
                        }
                    });
                }
            });
            this.btnCancel = new WUX.WButton(this.subId('bc'), GUI.TXT.CANCEL, GUI.ICO.CANCEL, WUX.BTN.ACT_OUTLINE_INFO);
            this.btnCancel.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    _this.btnCancel.blur();
                    return;
                }
                WUX.confirm(GUI.MSG.CONF_CANCEL, function (res) {
                    if (!res)
                        return;
                    if (_this.isNew) {
                        _this.fpDetail.clear();
                    }
                    else {
                        _this.onSelect();
                    }
                    _this.status = _this.iSTATUS_VIEW;
                    _this.fpDetail.enabled = false;
                    _this.selId = null;
                });
            });
            this.btnDelete = new WUX.WButton(this.subId('bd'), GUI.TXT.DELETE, GUI.ICO.DELETE, WUX.BTN.ACT_OUTLINE_DANGER);
            this.btnDelete.on('click', function (e) {
                _this.selId = null;
                _this.btnDelete.blur();
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var rd = _this.tabResult.getSelectedRowsData();
                if (!rd || !rd.length)
                    return;
                var ids = WUtil.getString(rd[0], GUI.ICredenziale.sID_SERVIZIO);
                var idc = WUtil.getString(rd[0], GUI.ICredenziale.sID_CREDENZIALE);
                WUX.confirm(GUI.MSG.CONF_DELETE, function (res) {
                    if (!res)
                        return;
                    jrpc.execute('CREDENZIALI.delete', [ids, idc], function (result) {
                        _this.btnFind.trigger('click');
                    });
                });
            });
            var rc = [
                ['Servizio', GUI.ICredenziale.sID_SERVIZIO],
                ['Credenziale', GUI.ICredenziale.sID_CREDENZIALE],
                ['Email', GUI.ICredenziale.sEMAIL]
            ];
            this.tabResult = new WUX.WDXTable(this.subId('tr'), WUtil.col(rc, 0), WUtil.col(rc, 1));
            this.tabResult.css({ h: 220 });
            this.tabResult.widths = [100];
            this.tabResult.onSelectionChanged(function (e) {
                _this.onSelect();
            });
            this.cntActions = new GUI.AppTableActions('ta');
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
                .end()
                .addBox()
                .addGroup({ classStyle: 'search-actions-and-results-wrapper' }, this.cntActions, this.tabResult)
                .end()
                .addRow()
                .addCol('12').section('Dettaglio')
                .add(this.tcoDetail);
            return this.container;
        };
        GUICredenziali.prototype.collapseHandler = function (e) {
            var c = WUtil.getBoolean(e.data, 'collapsed');
            if (c) {
                this.tagsFilter.setState({});
            }
            else {
                this.tagsFilter.setState(this.fpFilter.getValues(true));
            }
        };
        GUICredenziali.prototype.onSelect = function () {
            var _this = this;
            var item = WUtil.getItem(this.tabResult.getSelectedRowsData(), 0);
            if (!item)
                return;
            var ids = WUtil.getString(item, GUI.ICredenziale.sID_SERVIZIO);
            var idc = WUtil.getString(item, GUI.ICredenziale.sID_CREDENZIALE);
            if (!ids || !idc)
                return;
            this.fpDetail.clear();
            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.fpDetail.enabled = false;
            }
            jrpc.execute('CREDENZIALI.read', [ids, idc], function (result) {
                _this.fpDetail.setState(result);
                _this.status = _this.iSTATUS_VIEW;
            });
        };
        return GUICredenziali;
    }(WUX.WComponent));
    GUI.GUICredenziali = GUICredenziali;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var DlgAttCon = (function (_super) {
        __extends(DlgAttCon, _super);
        function DlgAttCon(id) {
            var _this = _super.call(this, id, 'DlgAttCon') || this;
            _this.title = 'Configurazione';
            _this.fp = new WUX.WFormPanel(_this.subId('fp'));
            _this.fp.addRow();
            _this.fp.addTextField(GUI.IAtt.sCONF_OPZIONE, 'Opzione');
            _this.fp.addRow();
            _this.fp.addTextField(GUI.IAtt.sCONF_DESCRIZIONE, 'Descrizione');
            _this.fp.addRow();
            _this.fp.addTextField(GUI.IAtt.sCONF_VALORI, 'Valori');
            _this.fp.addRow();
            _this.fp.addTextField(GUI.IAtt.sCONF_PREDEFINITO, 'Predefinito');
            _this.fp.setMandatory(GUI.IAtt.sCONF_OPZIONE, GUI.IAtt.sCONF_DESCRIZIONE);
            _this.body
                .addRow()
                .addCol('12')
                .add(_this.fp);
            return _this;
        }
        DlgAttCon.prototype.updateState = function (nextState) {
            _super.prototype.updateState.call(this, nextState);
            if (this.fp) {
                this.fp.clear();
                this.fp.setState(this.state);
            }
        };
        DlgAttCon.prototype.getState = function () {
            if (this.fp) {
                this.state = this.fp.getState();
            }
            return this.state;
        };
        DlgAttCon.prototype.onClickOk = function () {
            var check = this.fp.checkMandatory(true);
            if (check) {
                WUX.showWarning('Specificare: ' + check);
                return false;
            }
            return true;
        };
        return DlgAttCon;
    }(WUX.WDialog));
    GUI.DlgAttCon = DlgAttCon;
    var DlgAttPar = (function (_super) {
        __extends(DlgAttPar, _super);
        function DlgAttPar(id) {
            var _this = _super.call(this, id, 'DlgAttPar') || this;
            _this.title = 'Parametro';
            _this.fp = new WUX.WFormPanel(_this.subId('fp'));
            _this.fp.addRow();
            _this.fp.addTextField(GUI.IAtt.sPAR_PARAMETRO, 'Parametro');
            _this.fp.addRow();
            _this.fp.addTextField(GUI.IAtt.sPAR_DESCRIZIONE, 'Descrizione');
            _this.fp.addRow();
            _this.fp.addTextField(GUI.IAtt.sPAR_VALORI, 'Valori');
            _this.fp.addRow();
            _this.fp.addTextField(GUI.IAtt.sPAR_PREDEFINITO, 'Predefinito');
            _this.fp.setMandatory(GUI.IAtt.sPAR_PARAMETRO, GUI.IAtt.sPAR_DESCRIZIONE);
            _this.body
                .addRow()
                .addCol('12')
                .add(_this.fp);
            return _this;
        }
        DlgAttPar.prototype.updateState = function (nextState) {
            _super.prototype.updateState.call(this, nextState);
            if (this.fp) {
                this.fp.clear();
                this.fp.setState(this.state);
            }
        };
        DlgAttPar.prototype.getState = function () {
            if (this.fp) {
                this.state = this.fp.getState();
            }
            return this.state;
        };
        DlgAttPar.prototype.onClickOk = function () {
            var check = this.fp.checkMandatory(true);
            if (check) {
                WUX.showWarning('Specificare: ' + check);
                return false;
            }
            return true;
        };
        return DlgAttPar;
    }(WUX.WDialog));
    GUI.DlgAttPar = DlgAttPar;
    var DlgAttNot = (function (_super) {
        __extends(DlgAttNot, _super);
        function DlgAttNot(id) {
            var _this = _super.call(this, id, 'DlgAttNot') || this;
            _this.title = 'Notifica';
            _this.fp = new WUX.WFormPanel(_this.subId('fp'));
            _this.fp.addRow();
            _this.fp.addComponent(GUI.IAtt.sNOT_EVENTO, 'Evento', new GUI.LJSASelEventi());
            _this.fp.addRow();
            _this.fp.addTextField(GUI.IAtt.sNOT_DESTINAZIONE, 'Destinazione');
            _this.fp.setMandatory(GUI.IAtt.sNOT_EVENTO, GUI.IAtt.sNOT_DESTINAZIONE);
            _this.body
                .addRow()
                .addCol('12')
                .add(_this.fp);
            return _this;
        }
        DlgAttNot.prototype.updateState = function (nextState) {
            _super.prototype.updateState.call(this, nextState);
            if (this.fp) {
                this.fp.clear();
                this.fp.setState(this.state);
            }
        };
        DlgAttNot.prototype.getState = function () {
            if (this.fp) {
                this.state = this.fp.getState();
            }
            return this.state;
        };
        DlgAttNot.prototype.onClickOk = function () {
            var check = this.fp.checkMandatory(true);
            if (check) {
                WUX.showWarning('Specificare: ' + check);
                return false;
            }
            return true;
        };
        return DlgAttNot;
    }(WUX.WDialog));
    GUI.DlgAttNot = DlgAttNot;
    var DlgSchedCon = (function (_super) {
        __extends(DlgSchedCon, _super);
        function DlgSchedCon(id) {
            var _this = _super.call(this, id, 'DlgSchedCon') || this;
            _this.title = 'Configurazione';
            _this.fp = new WUX.WFormPanel(_this.subId('fp'));
            _this.fp.addRow();
            _this.fp.addTextField(GUI.ISched.sCONF_OPZIONE, 'Opzione');
            _this.fp.addRow();
            _this.fp.addTextField(GUI.ISched.sCONF_DESCRIZIONE, 'Descrizione', true);
            _this.fp.addRow();
            _this.fp.addTextField(GUI.ISched.sCONF_VALORI, 'Valori', true);
            _this.fp.addRow();
            _this.fp.addTextField(GUI.ISched.sCONF_VALORE, 'Valore');
            _this.fp.setMandatory(GUI.ISched.sCONF_OPZIONE, GUI.ISched.sCONF_DESCRIZIONE);
            _this.body
                .addRow()
                .addCol('12')
                .add(_this.fp);
            return _this;
        }
        DlgSchedCon.prototype.updateState = function (nextState) {
            _super.prototype.updateState.call(this, nextState);
            if (this.fp) {
                this.fp.clear();
                this.fp.setState(this.state);
            }
        };
        DlgSchedCon.prototype.getState = function () {
            if (this.fp) {
                this.state = this.fp.getState();
            }
            return this.state;
        };
        DlgSchedCon.prototype.onClickOk = function () {
            var check = this.fp.checkMandatory(true);
            if (check) {
                WUX.showWarning('Specificare: ' + check);
                return false;
            }
            return true;
        };
        return DlgSchedCon;
    }(WUX.WDialog));
    GUI.DlgSchedCon = DlgSchedCon;
    var DlgSchedPar = (function (_super) {
        __extends(DlgSchedPar, _super);
        function DlgSchedPar(id) {
            var _this = _super.call(this, id, 'DlgSchedPar') || this;
            _this.title = 'Parametro';
            _this.fp = new WUX.WFormPanel(_this.subId('fp'));
            _this.fp.addRow();
            _this.fp.addTextField(GUI.ISched.sPAR_PARAMETRO, 'Parametro');
            _this.fp.addRow();
            _this.fp.addTextField(GUI.ISched.sPAR_DESCRIZIONE, 'Descrizione', true);
            _this.fp.addRow();
            _this.fp.addTextField(GUI.ISched.sPAR_VALORI, 'Valori', true);
            _this.fp.addRow();
            _this.fp.addTextField(GUI.ISched.sPAR_VALORE, 'Valore');
            _this.fp.setMandatory(GUI.ISched.sPAR_PARAMETRO, GUI.ISched.sPAR_DESCRIZIONE);
            _this.body
                .addRow()
                .addCol('12')
                .add(_this.fp);
            return _this;
        }
        DlgSchedPar.prototype.updateState = function (nextState) {
            _super.prototype.updateState.call(this, nextState);
            if (this.fp) {
                this.fp.clear();
                this.fp.setState(this.state);
            }
        };
        DlgSchedPar.prototype.getState = function () {
            if (this.fp) {
                this.state = this.fp.getState();
            }
            return this.state;
        };
        DlgSchedPar.prototype.onClickOk = function () {
            var check = this.fp.checkMandatory(true);
            if (check) {
                WUX.showWarning('Specificare: ' + check);
                return false;
            }
            return true;
        };
        return DlgSchedPar;
    }(WUX.WDialog));
    GUI.DlgSchedPar = DlgSchedPar;
    var DlgSchedNot = (function (_super) {
        __extends(DlgSchedNot, _super);
        function DlgSchedNot(id) {
            var _this = _super.call(this, id, 'DlgSchedNot') || this;
            _this.title = 'Notifica';
            _this.fp = new WUX.WFormPanel(_this.subId('fp'));
            _this.fp.addRow();
            _this.fp.addComponent(GUI.ISched.sNOT_EVENTO, 'Evento', new GUI.LJSASelEventi());
            _this.fp.addRow();
            _this.fp.addTextField(GUI.ISched.sNOT_DESTINAZIONE, 'Destinazione');
            _this.fp.setMandatory(GUI.ISched.sNOT_EVENTO, GUI.ISched.sNOT_DESTINAZIONE);
            _this.body
                .addRow()
                .addCol('12')
                .add(_this.fp);
            return _this;
        }
        DlgSchedNot.prototype.updateState = function (nextState) {
            _super.prototype.updateState.call(this, nextState);
            if (this.fp) {
                this.fp.clear();
                this.fp.setState(this.state);
            }
        };
        DlgSchedNot.prototype.getState = function () {
            if (this.fp) {
                this.state = this.fp.getState();
            }
            return this.state;
        };
        DlgSchedNot.prototype.onClickOk = function () {
            var check = this.fp.checkMandatory(true);
            if (check) {
                WUX.showWarning('Specificare: ' + check);
                return false;
            }
            return true;
        };
        return DlgSchedNot;
    }(WUX.WDialog));
    GUI.DlgSchedNot = DlgSchedNot;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var WUtil = WUX.WUtil;
    var GUISchedulazioni = (function (_super) {
        __extends(GUISchedulazioni, _super);
        function GUISchedulazioni(id) {
            var _this = _super.call(this, id ? id : '*', 'GUISchedulazioni') || this;
            _this.iSTATUS_STARTUP = 0;
            _this.iSTATUS_VIEW = 1;
            _this.iSTATUS_EDITING = 2;
            _this.status = _this.iSTATUS_STARTUP;
            _this.dlgCon = new GUI.DlgSchedCon(_this.subId('dlgac'));
            _this.dlgCon.onHiddenModal(function (e) {
                if (!_this.dlgCon.ok)
                    return;
                var d = _this.tabCon.getState();
                var s = _this.dlgCon.getState();
                var i = WUtil.indexOf(d, GUI.ISched.sCONF_OPZIONE, s[GUI.ISched.sCONF_OPZIONE]);
                if (i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                _this.tabCon.setState(d);
            });
            _this.dlgPar = new GUI.DlgSchedPar(_this.subId('dlgap'));
            _this.dlgPar.onHiddenModal(function (e) {
                if (!_this.dlgPar.ok)
                    return;
                var d = _this.tabPar.getState();
                var s = _this.dlgPar.getState();
                var i = WUtil.indexOf(d, GUI.ISched.sPAR_PARAMETRO, s[GUI.ISched.sPAR_PARAMETRO]);
                if (i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                _this.tabPar.setState(d);
            });
            _this.dlgNot = new GUI.DlgSchedNot(_this.subId('dlgan'));
            _this.dlgNot.onHiddenModal(function (e) {
                if (!_this.dlgNot.ok)
                    return;
                var d = _this.tabNot.getState();
                var s = _this.dlgNot.getState();
                var i = GUI.indexOf(d, GUI.ISched.sNOT_EVENTO, GUI.ISched.sNOT_DESTINAZIONE, s[GUI.ISched.sCONF_OPZIONE] + ':' + s[GUI.ISched.sNOT_DESTINAZIONE]);
                if (i >= 0) {
                    d[i] = s;
                }
                else {
                    d.push(s);
                }
                _this.tabNot.setState(d);
            });
            return _this;
        }
        GUISchedulazioni.prototype.render = function () {
            var _this = this;
            this.btnFind = new WUX.WButton(this.subId('bf'), GUI.TXT.FIND, '', WUX.BTN.SM_PRIMARY);
            this.btnFind.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var check = _this.fpFilter.checkMandatory(true, true, false);
                if (check) {
                    WUX.showWarning('Specificare i seguenti campi: ' + check);
                    return;
                }
                var box = WUX.getComponent('boxFilter');
                if (box instanceof WUX.WBox) {
                    _this.tagsFilter.setState(_this.fpFilter.getValues(true));
                    box.collapse();
                }
                var user = GUI.getUserLogged();
                jrpc.execute('SCHEDULAZIONI.find', [_this.fpFilter.getState(), user.groups], function (result) {
                    _this.tabResult.setState(result);
                    _this.clearDet();
                    _this.status = _this.iSTATUS_STARTUP;
                    if (_this.selId) {
                        var idx_7 = WUtil.indexOf(result, GUI.ISched.sID, _this.selId);
                        if (idx_7 >= 0) {
                            setTimeout(function () {
                                _this.tabResult.select([idx_7]);
                            }, 100);
                        }
                        _this.selId = null;
                    }
                });
            });
            this.btnReset = new WUX.WButton(this.subId('br'), GUI.TXT.RESET, '', WUX.BTN.SM_SECONDARY);
            this.btnReset.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                _this.fpFilter.clear();
                _this.tagsFilter.setState({});
                _this.tabResult.setState([]);
                _this.clearDet();
                _this.status = _this.iSTATUS_STARTUP;
            });
            this.fpFilter = new WUX.WFormPanel(this.subId('ff'));
            this.fpFilter.addRow();
            this.fpFilter.addComponent(GUI.ISched.sID_SERVIZIO, 'Servizio', new GUI.LJSASelServizi());
            this.fpFilter.addTextField(GUI.ISched.sID_ATTIVITA, 'Attivita\'');
            this.fpFilter.addComponent(GUI.ISched.sSTATO, 'Stato', new GUI.LJSASelStati());
            this.fpFilter.addRow();
            this.fpFilter.addTextField(GUI.ISched.sID_CREDENZIALE_INS, 'Cred. Ins.');
            this.fpFilter.addDateField(GUI.ISched.sINIZIO_VALIDITA, 'Inizio Val.');
            this.fpFilter.addDateField(GUI.ISched.sFINE_VALIDITA, 'Fine Val.');
            this.selSerDet = new GUI.LJSASelServizi();
            this.selAttDet = new GUI.LJSASelAttivita();
            this.selSerDet.on('statechange', function (e) {
                _this.selAttDet.service = _this.selSerDet.getState();
            });
            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addComponent(GUI.ISched.sID_SERVIZIO, 'Servizio', this.selSerDet);
            this.fpDetail.addComponent(GUI.ISched.sID_ATTIVITA, 'Attivita\'', this.selAttDet);
            this.fpDetail.addRow();
            this.fpDetail.addTextField(GUI.ISched.sDESCRIZIONE, 'Descrizione');
            this.fpDetail.addTextField(GUI.ISched.sSCHEDULAZIONE, 'Schedulazione');
            this.fpDetail.addRow();
            this.fpDetail.addDateField(GUI.ISched.sINIZIO_VALIDITA, 'Inizio Val.');
            this.fpDetail.addDateField(GUI.ISched.sFINE_VALIDITA, 'Fine Val.');
            this.fpDetail.addInternalField(GUI.ISched.sID);
            this.fpDetail.addInternalField(GUI.ISched.sSTATO);
            this.fpDetail.addInternalField(GUI.ISched.sID_CREDENZIALE_INS);
            this.fpDetail.addInternalField(GUI.ISched.sDATA_INS);
            this.fpDetail.addInternalField(GUI.ISched.sORA_INS);
            this.fpDetail.addInternalField(GUI.ISched.sESEC_COMPLETATE);
            this.fpDetail.addInternalField(GUI.ISched.sESEC_INTERROTTE);
            this.fpDetail.enabled = false;
            this.fpFilter.onEnterPressed(function (e) {
                _this.btnFind.trigger('click');
            });
            this.btnNew = new WUX.WButton(this.subId('bn'), GUI.TXT.NEW, '', WUX.BTN.SM_INFO);
            this.btnNew.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    _this.btnNew.blur();
                    return;
                }
                _this.isNew = true;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.tabResult.clearSelection();
                _this.enableDet(true);
                _this.clearDet();
                _this.selSerDet.setState(GUI._defService);
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnOpen = new WUX.WButton(this.subId('bo'), GUI.TXT.OPEN, GUI.ICO.OPEN, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnOpen.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    _this.btnOpen.blur();
                    return;
                }
                var sr = _this.tabResult.getSelectedRows();
                if (!sr || !sr.length) {
                    WUX.showWarning('Seleziona l\'elemento da modificare');
                    _this.btnOpen.blur();
                    return;
                }
                _this.isNew = false;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.enableDet(true);
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnSave = new WUX.WButton(this.subId('bs'), GUI.TXT.SAVE, GUI.ICO.SAVE, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnSave.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    WUX.showWarning('Cliccare su Modifica.');
                    _this.btnSave.blur();
                    return;
                }
                var check = _this.fpDetail.checkMandatory(true);
                if (check) {
                    _this.btnSave.blur();
                    WUX.showWarning('Specificare: ' + check);
                    return;
                }
                var values = _this.fpDetail.getState();
                values[GUI.ISched.sCONFIGURAZIONE] = _this.tabCon.getState();
                values[GUI.ISched.sPARAMETRI] = _this.tabPar.getState();
                values[GUI.ISched.sNOTIFICA] = _this.tabNot.getState();
                GUI.putUserLog(values);
                if (_this.isNew) {
                    jrpc.execute('SCHEDULAZIONI.insert', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.enableDet(false);
                        _this.selId = result[GUI.ISched.sID];
                        _this.btnFind.trigger('click');
                    });
                }
                else {
                    jrpc.execute('SCHEDULAZIONI.update', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.enableDet(false);
                        _this.selId = result[GUI.ISched.sID];
                        var selRows = _this.tabResult.getSelectedRows();
                        if (!selRows || !selRows.length) {
                            _this.btnFind.trigger('click');
                        }
                        else {
                            var idx_8 = selRows[0];
                            var records = _this.tabResult.getState();
                            records[idx_8] = result;
                            _this.tabResult.refresh();
                            setTimeout(function () {
                                _this.tabResult.select([idx_8]);
                            }, 100);
                        }
                    });
                }
            });
            this.btnCancel = new WUX.WButton(this.subId('bc'), GUI.TXT.CANCEL, GUI.ICO.CANCEL, WUX.BTN.ACT_OUTLINE_INFO);
            this.btnCancel.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    _this.btnCancel.blur();
                    return;
                }
                WUX.confirm(GUI.MSG.CONF_CANCEL, function (res) {
                    if (!res)
                        return;
                    if (_this.isNew) {
                        _this.clearDet();
                    }
                    else {
                        _this.onSelect();
                    }
                    _this.status = _this.iSTATUS_VIEW;
                    _this.enableDet(false);
                    _this.selId = null;
                });
            });
            this.btnDelete = new WUX.WButton(this.subId('bd'), GUI.TXT.DELETE, GUI.ICO.DELETE, WUX.BTN.ACT_OUTLINE_DANGER);
            this.btnDelete.on('click', function (e) {
                _this.selId = null;
                _this.btnDelete.blur();
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var rd = _this.tabResult.getSelectedRowsData();
                if (!rd || !rd.length)
                    return;
                var ids = WUtil.getString(rd[0], GUI.ISched.sID_SERVIZIO);
                var idc = WUtil.getNumber(rd[0], GUI.ISched.sID);
                var usr = GUI.getUserLogged().userName;
                WUX.confirm(GUI.MSG.CONF_DELETE, function (res) {
                    if (!res)
                        return;
                    jrpc.execute('SCHEDULAZIONI.delete', [ids, idc, usr], function (result) {
                        _this.btnFind.trigger('click');
                    });
                });
            });
            this.btnToggle = new WUX.WButton(this.subId('bt'), 'Abilita', WUX.WIcon.THUMBS_O_UP, WUX.BTN.ACT_OUTLINE_DANGER);
            this.btnToggle.on('click', function (e) {
                _this.btnToggle.blur();
                var rd = _this.tabResult.getSelectedRowsData();
                if (!rd || !rd.length) {
                    WUX.showWarning('Selezione l\'elemento da abilitare/disabilitare');
                    return;
                }
                var ids = WUtil.getString(rd[0], GUI.ISched.sID_SERVIZIO);
                var idc = WUtil.getNumber(rd[0], GUI.ISched.sID);
                var cst = WUtil.getString(rd[0], GUI.ISched.sSTATO);
                var flg = !(cst == 'D');
                var usr = GUI.getUserLogged().userName;
                _this.selId = idc;
                jrpc.execute('SCHEDULAZIONI.setEnabled', [ids, idc, flg, usr], function (result) {
                    var sr = _this.tabResult.getSelectedRows();
                    if (!sr || !sr.length) {
                        _this.btnFind.trigger('click');
                    }
                    else {
                        var r = _this.tabResult.getState();
                        var x_1 = sr[0];
                        if (flg) {
                            r[x_1][GUI.ISched.sSTATO] = 'A';
                            _this.btnToggle.setText('Disabilita', WUX.WIcon.THUMBS_O_DOWN);
                            _this.btnToggle.setText('Disabilita', WUX.WIcon.THUMBS_O_DOWN);
                        }
                        else {
                            r[x_1][GUI.ISched.sSTATO] = 'D';
                            _this.btnToggle.setText('Abilita', WUX.WIcon.THUMBS_O_UP);
                            _this.btnToggle.setText('Abilita', WUX.WIcon.THUMBS_O_UP);
                        }
                        _this.tabResult.refresh();
                        setTimeout(function () {
                            _this.tabResult.select([x_1]);
                        }, 100);
                    }
                });
            });
            var rc = [
                ['Id', GUI.ISched.sID],
                ['Servizio', GUI.ISched.sID_SERVIZIO],
                ['Attivita\'', GUI.ISched.sID_ATTIVITA],
                ['Schedulazione', GUI.ISched.sSCHEDULAZIONE],
                ['Stato', GUI.ISched.sSTATO],
                ['Descrizione', GUI.ISched.sDESCRIZIONE],
                ['Esec. Compl.', GUI.ISched.sESEC_COMPLETATE],
                ['Esec. Int.', GUI.ISched.sESEC_INTERROTTE],
                ['Id Cred. Ins.', GUI.ISched.sID_CREDENZIALE_INS],
                ['Data Ins.', GUI.ISched.sDATA_INS],
                ['Ora Ins.', GUI.ISched.sORA_INS],
                ['Id Cred. Agg.', GUI.ISched.sID_CREDENZIALE_AGG],
                ['Data Agg.', GUI.ISched.sDATA_AGG],
                ['Ora Agg.', GUI.ISched.sORA_AGG]
            ];
            this.tabResult = new WUX.WDXTable(this.subId('tr'), WUtil.col(rc, 0), WUtil.col(rc, 1));
            this.tabResult.css({ h: 220 });
            this.tabResult.widths = [100];
            this.tabResult.onSelectionChanged(function (e) {
                _this.onSelect();
            });
            this.tabResult.onRowPrepared(function (e) {
                if (!e.data)
                    return;
                var s = WUtil.getString(e.data, GUI.ISched.sSTATO);
                if (s == 'D') {
                    WUX.setCss(e.rowElement, WUX.CSS.ERROR);
                }
                else if (s == 'E') {
                    WUX.setCss(e.rowElement, WUX.CSS.SUCCESS);
                }
            });
            this.tabCon = new WUX.WDXTable(this.subId('tbc'), ['Opzione', 'Descrizione', 'Valore'], [GUI.ISched.sCONF_OPZIONE, GUI.ISched.sCONF_DESCRIZIONE, GUI.ISched.sCONF_VALORE]);
            this.tabCon.selectionMode = 'single';
            this.tabCon.css({ h: 240 });
            this.tabCon.onDoubleClick(function (e) {
                var s = _this.tabCon.getSelectedRowsData();
                if (!s || !s.length)
                    return;
                _this.dlgCon.setState(s[0]);
                _this.dlgCon.show(_this);
            });
            this.btnAddCon = new WUX.WButton(this.subId('bac'), GUI.TXT.ADD, '', WUX.BTN.SM_PRIMARY);
            this.btnAddCon.on('click', function (e) {
                _this.dlgCon.setState(null);
                _this.dlgCon.show(_this);
            });
            this.btnRemCon = new WUX.WButton(this.subId('brc'), GUI.TXT.REMOVE, '', WUX.BTN.SM_DANGER);
            this.btnRemCon.on('click', function (e) {
                var s = _this.tabCon.getSelectedRows();
                if (!s || !s.length)
                    return;
                var d = _this.tabCon.getState();
                d.splice(s[0], 1);
                _this.tabCon.setState(d);
            });
            this.tabPar = new WUX.WDXTable(this.subId('tbp'), ['Parametro', 'Descrizione', 'Valore'], [GUI.ISched.sPAR_PARAMETRO, GUI.ISched.sPAR_DESCRIZIONE, GUI.ISched.sPAR_VALORE]);
            this.tabPar.selectionMode = 'single';
            this.tabPar.css({ h: 240 });
            this.tabPar.onDoubleClick(function (e) {
                var s = _this.tabPar.getSelectedRowsData();
                if (!s || !s.length)
                    return;
                _this.dlgPar.setState(s[0]);
                _this.dlgPar.show(_this);
            });
            this.btnAddPar = new WUX.WButton(this.subId('bap'), GUI.TXT.ADD, '', WUX.BTN.SM_PRIMARY);
            this.btnAddPar.on('click', function (e) {
                _this.dlgPar.setState(null);
                _this.dlgPar.show(_this);
            });
            this.btnRemPar = new WUX.WButton(this.subId('brp'), GUI.TXT.REMOVE, '', WUX.BTN.SM_DANGER);
            this.btnRemPar.on('click', function (e) {
                var s = _this.tabPar.getSelectedRows();
                if (!s || !s.length)
                    return;
                var d = _this.tabPar.getState();
                d.splice(s[0], 1);
                _this.tabPar.setState(d);
            });
            this.tabNot = new WUX.WDXTable(this.subId('tbn'), ['Evento', 'Destinazione'], [GUI.ISched.sNOT_EVENTO, GUI.ISched.sNOT_DESTINAZIONE]);
            this.tabNot.selectionMode = 'single';
            this.tabNot.css({ h: 240 });
            this.tabNot.onDoubleClick(function (e) {
                var s = _this.tabPar.getSelectedRowsData();
                if (!s || !s.length)
                    return;
                _this.dlgNot.setState(s[0]);
                _this.dlgNot.show(_this);
            });
            this.btnAddNot = new WUX.WButton(this.subId('ban'), GUI.TXT.ADD, '', WUX.BTN.SM_PRIMARY);
            this.btnAddNot.on('click', function (e) {
                _this.dlgNot.setState(null);
                _this.dlgNot.show(_this);
            });
            this.btnRemNot = new WUX.WButton(this.subId('brn'), GUI.TXT.REMOVE, '', WUX.BTN.SM_DANGER);
            this.btnRemNot.on('click', function (e) {
                var s = _this.tabNot.getSelectedRows();
                if (!s || !s.length)
                    return;
                var d = _this.tabNot.getState();
                d.splice(s[0], 1);
                _this.tabNot.setState(d);
            });
            this.cntActions = new GUI.AppTableActions('ta');
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
            this.tcoDetail.on('statechange', function (e) {
                var itab = _this.tcoDetail.getState();
                switch (itab) {
                    case 0:
                        break;
                    case 1:
                        _this.tabCon.repaint();
                        break;
                    case 2:
                        _this.tabPar.repaint();
                        break;
                    case 3:
                        _this.tabNot.repaint();
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
                .end()
                .addBox()
                .addGroup({ classStyle: 'search-actions-and-results-wrapper' }, this.cntActions, this.tabResult)
                .end()
                .addRow()
                .addCol('12').section('Dettaglio')
                .add(this.tcoDetail);
            return this.container;
        };
        GUISchedulazioni.prototype.collapseHandler = function (e) {
            var c = WUtil.getBoolean(e.data, 'collapsed');
            if (c) {
                this.tagsFilter.setState({});
            }
            else {
                this.tagsFilter.setState(this.fpFilter.getValues(true));
            }
        };
        GUISchedulazioni.prototype.onSelect = function () {
            var _this = this;
            var item = WUtil.getItem(this.tabResult.getSelectedRowsData(), 0);
            if (!item)
                return;
            var id = WUtil.getNumber(item, GUI.ISched.sID);
            if (!id)
                return;
            this.clearDet();
            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.enableDet(false);
            }
            jrpc.execute('SCHEDULAZIONI.read', [id], function (result) {
                result[GUI.ISched.sID_SERVIZIO] = GUI.getIdVal(result, GUI.ISched.sID_SERVIZIO);
                result[GUI.ISched.sID_ATTIVITA] = GUI.getIdVal(result, GUI.ISched.sID_ATTIVITA);
                _this.fpDetail.setState(result);
                _this.tabCon.setState(WUtil.getArray(result, GUI.ISched.sCONFIGURAZIONE));
                _this.tabPar.setState(WUtil.getArray(result, GUI.ISched.sPARAMETRI));
                _this.tabNot.setState(WUtil.getArray(result, GUI.ISched.sNOTIFICA));
                var cst = WUtil.getString(result, GUI.ISched.sSTATO);
                if (cst == 'D') {
                    _this.btnToggle.setText('Abilita', WUX.WIcon.THUMBS_O_UP);
                    _this.btnToggle.setText('Abilita', WUX.WIcon.THUMBS_O_UP);
                }
                else {
                    _this.btnToggle.setText('Disabilita', WUX.WIcon.THUMBS_O_DOWN);
                    _this.btnToggle.setText('Disabilita', WUX.WIcon.THUMBS_O_DOWN);
                }
                _this.status = _this.iSTATUS_VIEW;
            });
        };
        GUISchedulazioni.prototype.clearDet = function () {
            this.fpDetail.clear();
            this.tabCon.setState([]);
            this.tabPar.setState([]);
            this.tabNot.setState([]);
        };
        GUISchedulazioni.prototype.enableDet = function (e) {
            this.fpDetail.enabled = e;
            this.tabCon.enabled = e;
            this.tabPar.enabled = e;
            this.tabNot.enabled = e;
        };
        return GUISchedulazioni;
    }(WUX.WComponent));
    GUI.GUISchedulazioni = GUISchedulazioni;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var WUtil = WUX.WUtil;
    var GUIServizi = (function (_super) {
        __extends(GUIServizi, _super);
        function GUIServizi(id) {
            var _this = _super.call(this, id ? id : '*', 'GUIServizi') || this;
            _this.iSTATUS_STARTUP = 0;
            _this.iSTATUS_VIEW = 1;
            _this.iSTATUS_EDITING = 2;
            _this.status = _this.iSTATUS_STARTUP;
            return _this;
        }
        GUIServizi.prototype.render = function () {
            var _this = this;
            this.btnFind = new WUX.WButton(this.subId('bf'), GUI.TXT.FIND, '', WUX.BTN.SM_PRIMARY);
            this.btnFind.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var check = _this.fpFilter.checkMandatory(true, true, false);
                if (check) {
                    WUX.showWarning('Specificare i seguenti campi: ' + check);
                    return;
                }
                var box = WUX.getComponent('boxFilter');
                if (box instanceof WUX.WBox) {
                    _this.tagsFilter.setState(_this.fpFilter.getValues(true));
                    box.collapse();
                }
                var user = GUI.getUserLogged();
                jrpc.execute('SERVIZI.find', [_this.fpFilter.getState(), user.groups], function (result) {
                    _this.tabResult.setState(result);
                    _this.fpDetail.clear();
                    _this.status = _this.iSTATUS_STARTUP;
                    if (_this.selId) {
                        var idx_9 = WUtil.indexOf(result, GUI.IServizio.sID_SERVIZIO, _this.selId);
                        if (idx_9 >= 0) {
                            setTimeout(function () {
                                _this.tabResult.select([idx_9]);
                            }, 100);
                        }
                        _this.selId = null;
                    }
                });
            });
            this.btnReset = new WUX.WButton(this.subId('br'), GUI.TXT.RESET, '', WUX.BTN.SM_SECONDARY);
            this.btnReset.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                _this.fpFilter.clear();
                _this.tagsFilter.setState({});
                _this.tabResult.setState([]);
                _this.fpDetail.clear();
                _this.status = _this.iSTATUS_STARTUP;
            });
            this.fpFilter = new WUX.WFormPanel(this.subId('ff'));
            this.fpFilter.addRow();
            this.fpFilter.addTextField(GUI.IServizio.sID_SERVIZIO, 'Codice');
            this.fpFilter.addTextField(GUI.IServizio.sDESCRIZIONE, 'Descrizione');
            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addTextField(GUI.IServizio.sID_SERVIZIO, 'Codice');
            this.fpDetail.addTextField(GUI.IServizio.sDESCRIZIONE, 'Descrizione');
            this.fpDetail.enabled = false;
            this.fpFilter.onEnterPressed(function (e) {
                _this.btnFind.trigger('click');
            });
            this.btnNew = new WUX.WButton(this.subId('bn'), GUI.TXT.NEW, '', WUX.BTN.SM_INFO);
            this.btnNew.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING) {
                    _this.btnNew.blur();
                    return;
                }
                _this.isNew = true;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.tabResult.clearSelection();
                _this.fpDetail.enabled = true;
                _this.fpDetail.clear();
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnOpen = new WUX.WButton(this.subId('bo'), GUI.TXT.OPEN, GUI.ICO.OPEN, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnOpen.on('click', function (e) {
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    _this.btnOpen.blur();
                    return;
                }
                var sr = _this.tabResult.getSelectedRows();
                if (!sr || !sr.length) {
                    WUX.showWarning('Seleziona l\'elemento da modificare');
                    _this.btnOpen.blur();
                    return;
                }
                _this.isNew = false;
                _this.status = _this.iSTATUS_EDITING;
                _this.selId = null;
                _this.fpDetail.enabled = true;
                setTimeout(function () { _this.fpDetail.focus(); }, 100);
            });
            this.btnSave = new WUX.WButton(this.subId('bs'), GUI.TXT.SAVE, GUI.ICO.SAVE, WUX.BTN.ACT_OUTLINE_PRIMARY);
            this.btnSave.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    WUX.showWarning('Cliccare su Modifica.');
                    _this.btnSave.blur();
                    return;
                }
                var check = _this.fpDetail.checkMandatory(true);
                if (check) {
                    _this.btnSave.blur();
                    WUX.showWarning('Specificare: ' + check);
                    return;
                }
                var values = _this.fpDetail.getState();
                if (_this.isNew) {
                    jrpc.execute('SERVIZI.insert', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.IServizio.sID_SERVIZIO];
                        _this.btnFind.trigger('click');
                    });
                }
                else {
                    jrpc.execute('SERVIZI.update', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.IServizio.sID_SERVIZIO];
                        var selRows = _this.tabResult.getSelectedRows();
                        if (!selRows || !selRows.length) {
                            _this.btnFind.trigger('click');
                        }
                        else {
                            var idx_10 = selRows[0];
                            var records = _this.tabResult.getState();
                            records[idx_10] = result;
                            _this.tabResult.refresh();
                            setTimeout(function () {
                                _this.tabResult.select([idx_10]);
                            }, 100);
                        }
                    });
                }
            });
            this.btnCancel = new WUX.WButton(this.subId('bc'), GUI.TXT.CANCEL, GUI.ICO.CANCEL, WUX.BTN.ACT_OUTLINE_INFO);
            this.btnCancel.on('click', function (e) {
                if (_this.status != _this.iSTATUS_EDITING) {
                    _this.btnCancel.blur();
                    return;
                }
                WUX.confirm(GUI.MSG.CONF_CANCEL, function (res) {
                    if (!res)
                        return;
                    if (_this.isNew) {
                        _this.fpDetail.clear();
                    }
                    else {
                        _this.onSelect();
                    }
                    _this.status = _this.iSTATUS_VIEW;
                    _this.fpDetail.enabled = false;
                    _this.selId = null;
                });
            });
            this.btnDelete = new WUX.WButton(this.subId('bd'), GUI.TXT.DELETE, GUI.ICO.DELETE, WUX.BTN.ACT_OUTLINE_DANGER);
            this.btnDelete.on('click', function (e) {
                _this.selId = null;
                _this.btnDelete.blur();
                if (_this.status == _this.iSTATUS_EDITING || _this.status == _this.iSTATUS_STARTUP) {
                    WUX.showWarning('Elemento in fase di modifica.');
                    return;
                }
                var rd = _this.tabResult.getSelectedRowsData();
                if (!rd || !rd.length)
                    return;
                var id = WUtil.getString(rd[0], GUI.IServizio.sID_SERVIZIO);
                WUX.confirm(GUI.MSG.CONF_DELETE, function (res) {
                    if (!res)
                        return;
                    jrpc.execute('SERVIZI.delete', [id], function (result) {
                        _this.btnFind.trigger('click');
                    });
                });
            });
            var rc = [
                ['Codice', GUI.IServizio.sID_SERVIZIO],
                ['Descrizione', GUI.IServizio.sDESCRIZIONE]
            ];
            this.tabResult = new WUX.WDXTable(this.subId('tr'), WUtil.col(rc, 0), WUtil.col(rc, 1));
            this.tabResult.css({ h: 220 });
            this.tabResult.widths = [100];
            this.tabResult.onSelectionChanged(function (e) {
                _this.onSelect();
            });
            this.cntActions = new GUI.AppTableActions('ta');
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
                .end()
                .addBox()
                .addGroup({ classStyle: 'search-actions-and-results-wrapper' }, this.cntActions, this.tabResult)
                .end()
                .addRow()
                .addCol('12').section('Dettaglio')
                .add(this.tcoDetail);
            return this.container;
        };
        GUIServizi.prototype.collapseHandler = function (e) {
            var c = WUtil.getBoolean(e.data, 'collapsed');
            if (c) {
                this.tagsFilter.setState({});
            }
            else {
                this.tagsFilter.setState(this.fpFilter.getValues(true));
            }
        };
        GUIServizi.prototype.onSelect = function () {
            var _this = this;
            var item = WUtil.getItem(this.tabResult.getSelectedRowsData(), 0);
            if (!item)
                return;
            var id = WUtil.getString(item, GUI.IServizio.sID_SERVIZIO);
            if (!id)
                return;
            this.fpDetail.clear();
            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.fpDetail.enabled = false;
            }
            jrpc.execute('SERVIZI.read', [id], function (result) {
                _this.fpDetail.setState(result);
                _this.status = _this.iSTATUS_VIEW;
            });
        };
        return GUIServizi;
    }(WUX.WComponent));
    GUI.GUIServizi = GUIServizi;
})(GUI || (GUI = {}));
//# sourceMappingURL=ljsa.js.map