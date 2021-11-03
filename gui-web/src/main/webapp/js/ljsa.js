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
                    _this.fpDetail.clear();
                    _this.status = _this.iSTATUS_STARTUP;
                    if (_this.selId) {
                        var idx_1 = GUI.indexOf(result, GUI.IAttivita.sID_SERVIZIO, GUI.IAttivita.sID_ATTIVITA, _this.selId);
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
                _this.fpDetail.clear();
                _this.status = _this.iSTATUS_STARTUP;
            });
            this.fpFilter = new WUX.WFormPanel(this.subId('ff'));
            this.fpFilter.addRow();
            this.fpFilter.addComponent(GUI.IAttivita.sID_SERVIZIO, 'Servizio', new GUI.LJSASelServizi());
            this.fpFilter.addTextField(GUI.IAttivita.sID_ATTIVITA, 'Codice');
            this.fpFilter.addTextField(GUI.IAttivita.sDESCRIZIONE, 'Descrizione');
            this.selSerDet = new GUI.LJSASelServizi();
            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addComponent(GUI.IAttivita.sID_SERVIZIO, 'Servizio', this.selSerDet);
            this.fpDetail.addTextField(GUI.IAttivita.sID_ATTIVITA, 'Codice');
            this.fpDetail.addRow();
            this.fpDetail.addTextField(GUI.IAttivita.sCLASSE, 'Classe');
            this.fpDetail.addTextField(GUI.IAttivita.sDESCRIZIONE, 'Descrizione');
            this.fpDetail.addInternalField(GUI.IAttivita.sID_CREDENZIALE_INS);
            this.fpDetail.addInternalField(GUI.IAttivita.sDATA_INS);
            this.fpDetail.addInternalField(GUI.IAttivita.sORA_INS);
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
                    jrpc.execute('ATTIVITA.insert', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.IAttivita.sID_SERVIZIO] + ":" + result[GUI.IAttivita.sID_ATTIVITA];
                        _this.btnFind.trigger('click');
                    });
                }
                else {
                    jrpc.execute('ATTIVITA.update', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.IAttivita.sID_SERVIZIO] + ":" + result[GUI.IAttivita.sID_ATTIVITA];
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
                var ids = WUtil.getString(rd[0], GUI.IAttivita.sID_SERVIZIO);
                var ida = WUtil.getString(rd[0], GUI.IAttivita.sID_ATTIVITA);
                WUX.confirm(GUI.MSG.CONF_DELETE, function (res) {
                    if (!res)
                        return;
                    jrpc.execute('ATTIVITA.delete', [ids, ida], function (result) {
                        _this.btnFind.trigger('click');
                    });
                });
            });
            var rc = [
                ['Servizio', GUI.IAttivita.sID_SERVIZIO],
                ['Codice', GUI.IAttivita.sID_ATTIVITA],
                ['Descrizione', GUI.IAttivita.sDESCRIZIONE],
                ['Classe', GUI.IAttivita.sCLASSE],
                ['Id Cred. Ins.', GUI.IAttivita.sID_CREDENZIALE_INS],
                ['Data Ins.', GUI.IAttivita.sDATA_INS],
                ['Ora Ins.', GUI.IAttivita.sORA_INS],
                ['Id Cred. Agg.', GUI.IAttivita.sID_CREDENZIALE_AGG],
                ['Data Agg.', GUI.IAttivita.sDATA_AGG],
                ['Ora Agg.', GUI.IAttivita.sORA_AGG]
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
            var ids = WUtil.getString(item, GUI.IAttivita.sID_SERVIZIO);
            var ida = WUtil.getString(item, GUI.IAttivita.sID_ATTIVITA);
            if (!ids || !ida)
                return;
            this.fpDetail.clear();
            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.fpDetail.enabled = false;
            }
            jrpc.execute('ATTIVITA.read', [ids, ida], function (result) {
                _this.fpDetail.setState(result);
                _this.status = _this.iSTATUS_VIEW;
            });
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
    var IAttivita = (function () {
        function IAttivita() {
        }
        IAttivita.sID_SERVIZIO = "id_servizio";
        IAttivita.sID_ATTIVITA = "id_attivita";
        IAttivita.sDESCRIZIONE = "descrizione";
        IAttivita.sCLASSE = "classe";
        IAttivita.sATTIVO = "attivo";
        IAttivita.sID_CREDENZIALE_INS = "id_credenziale_ins";
        IAttivita.sDATA_INS = "data_inserimento";
        IAttivita.sORA_INS = "ora_inserimento";
        IAttivita.sID_CREDENZIALE_AGG = "id_credenziale_agg";
        IAttivita.sDATA_AGG = "data_aggiornamento";
        IAttivita.sORA_AGG = "ora_aggiornamento";
        IAttivita.sCONFIGURAZIONE = "configurazione";
        IAttivita.sCONF_OPZIONE = "opzione";
        IAttivita.sCONF_DESCRIZIONE = "descrizione";
        IAttivita.sCONF_VALORI = "valori";
        IAttivita.sCONF_PREDEFINITO = "predefinito";
        IAttivita.sPARAMETRI = "parametri";
        IAttivita.sPAR_PARAMETRO = "parametro";
        IAttivita.sPAR_DESCRIZIONE = "descrizione";
        IAttivita.sPAR_VALORI = "valori";
        IAttivita.sPAR_PREDEFINITO = "predefinito";
        IAttivita.sNOTIFICA = "notifica";
        IAttivita.sNOT_EVENTO = "evento";
        IAttivita.sNOT_DESTINAZIONE = "destinazione";
        return IAttivita;
    }());
    GUI.IAttivita = IAttivita;
    var ISched = (function () {
        function ISched() {
        }
        ISched.sID_SCHEDULAZIONE = "id_schedulazione";
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
        ISched.sPAR_DESCRIZIONE = IAttivita.sPAR_DESCRIZIONE;
        ISched.sPAR_VALORI = IAttivita.sPAR_VALORI;
        ISched.sPAR_PREDEFINITO = IAttivita.sPAR_PREDEFINITO;
        ISched.sCONFIGURAZIONE = "configurazione";
        ISched.sCONF_OPZIONE = "opzione";
        ISched.sCONF_VALORE = "valore";
        ISched.sCONF_DA_ATTIVITA = "da_attivita";
        ISched.sCONF_OVERWRITE = "overwrite";
        ISched.sCONF_DESCRIZIONE = IAttivita.sCONF_DESCRIZIONE;
        ISched.sCONF_VALORI = IAttivita.sCONF_VALORI;
        ISched.sCONF_PREDEFINITO = IAttivita.sCONF_PREDEFINITO;
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
            var $i = $('<div class="table-actions clearfix" data-b2x-sticky-element="1" data-b2x-sticky-element-z-index="3"></div>');
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
            if (!s)
                GUI._defService = s;
        };
        LJSASelServizi.prototype.componentDidMount = function () {
            var _this = this;
            var user = GUI.getUserLogged();
            jrpc.execute('SERVIZI.lookup', [user.groups], function (result) {
                var data = [];
                for (var i = 0; i < result.length; i++) {
                    var r = result[i];
                    var d = { id: r[0], text: r[2] };
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
                        jrpc.execute("CLASSI.lookup", [params.data], success);
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
            var _this = _super.call(this, id, [], multiple) || this;
            _this.name = 'LJSASelAttivita';
            return _this;
        }
        LJSASelAttivita.prototype.componentDidMount = function () {
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
                        jrpc.execute("ATTIVITA.lookup", [GUI._defService, params.data], success);
                        return undefined;
                    }
                },
                placeholder: "",
                allowClear: true,
                minimumInputLength: 3
            };
            this.init(options);
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
    var WUtil = WUX.WUtil;
    var GUISchedulazioni = (function (_super) {
        __extends(GUISchedulazioni, _super);
        function GUISchedulazioni(id) {
            var _this = _super.call(this, id ? id : '*', 'GUISchedulazioni') || this;
            _this.iSTATUS_STARTUP = 0;
            _this.iSTATUS_VIEW = 1;
            _this.iSTATUS_EDITING = 2;
            _this.status = _this.iSTATUS_STARTUP;
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
                    _this.fpDetail.clear();
                    _this.status = _this.iSTATUS_STARTUP;
                    if (_this.selId) {
                        var idx_7 = WUtil.indexOf(result, GUI.ISched.sID_SCHEDULAZIONE, _this.selId);
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
                _this.fpDetail.clear();
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
            this.fpDetail = new WUX.WFormPanel(this.subId('fpd'));
            this.fpDetail.addRow();
            this.fpDetail.addComponent(GUI.ISched.sID_SERVIZIO, 'Servizio', this.selSerDet);
            this.fpDetail.addTextField(GUI.ISched.sID_ATTIVITA, 'Attivita\'');
            this.fpDetail.addRow();
            this.fpDetail.addTextField(GUI.ISched.sDESCRIZIONE, 'Descrizione');
            this.fpDetail.addTextField(GUI.ISched.sSCHEDULAZIONE, 'Schedulazione');
            this.fpDetail.addRow();
            this.fpDetail.addDateField(GUI.ISched.sINIZIO_VALIDITA, 'Inizio Val.');
            this.fpDetail.addDateField(GUI.ISched.sFINE_VALIDITA, 'Fine Val.');
            this.fpDetail.addInternalField(GUI.ISched.sID_SCHEDULAZIONE);
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
                    jrpc.execute('SCHEDULAZIONI.insert', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.ISched.sID_SCHEDULAZIONE];
                        _this.btnFind.trigger('click');
                    });
                }
                else {
                    jrpc.execute('SCHEDULAZIONI.update', [values], function (result) {
                        _this.status = _this.iSTATUS_VIEW;
                        _this.fpDetail.enabled = false;
                        _this.selId = result[GUI.ISched.sID_SCHEDULAZIONE];
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
                var ids = WUtil.getString(rd[0], GUI.ISched.sID_SERVIZIO);
                var idc = WUtil.getNumber(rd[0], GUI.ISched.sID_SCHEDULAZIONE);
                var usr = GUI.getUserLogged().userName;
                WUX.confirm(GUI.MSG.CONF_DELETE, function (res) {
                    if (!res)
                        return;
                    jrpc.execute('SCHEDULAZIONI.delete', [ids, idc, usr], function (result) {
                        _this.btnFind.trigger('click');
                    });
                });
            });
            var rc = [
                ['Id', GUI.ISched.sID_SCHEDULAZIONE],
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
            var id = WUtil.getNumber(item, GUI.ISched.sID_SCHEDULAZIONE);
            if (!id)
                return;
            this.fpDetail.clear();
            if (this.status == this.iSTATUS_EDITING) {
                WUX.showWarning('Modifiche annullate');
                this.fpDetail.enabled = false;
            }
            jrpc.execute('SCHEDULAZIONI.read', [id], function (result) {
                _this.fpDetail.setState(result);
                _this.status = _this.iSTATUS_VIEW;
            });
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