var GUI;
(function (GUI) {
    var GUIAttivita = (function (_super) {
        __extends(GUIAttivita, _super);
        function GUIAttivita(id) {
            return _super.call(this, id ? id : '*', 'GUIAttivita') || this;
        }
        GUIAttivita.prototype.render = function () {
            return '<p>GUIAttivita</p>';
        };
        return GUIAttivita;
    }(WUX.WComponent));
    GUI.GUIAttivita = GUIAttivita;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    GUI.LJSA_SERVICE = 'LJSA';
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
})(GUI || (GUI = {}));
WUX.global.locale = GUI.getLocale();
var jrpc = new JRPC("/LJSA/rpc");
jrpc.setUserName(GUI.getUserLogged().userName);
jrpc.setPassword(GUI.getUserLogged().tokenId);
var GUI;
(function (GUI) {
    var GUIClassi = (function (_super) {
        __extends(GUIClassi, _super);
        function GUIClassi(id) {
            return _super.call(this, id ? id : '*', 'GUIClassi') || this;
        }
        GUIClassi.prototype.render = function () {
            return '<p>GUIClassi</p>';
        };
        return GUIClassi;
    }(WUX.WComponent));
    GUI.GUIClassi = GUIClassi;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
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
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var GUICredenziali = (function (_super) {
        __extends(GUICredenziali, _super);
        function GUICredenziali(id) {
            return _super.call(this, id ? id : '*', 'GUICredenziali') || this;
        }
        GUICredenziali.prototype.render = function () {
            return '<p>GUICredenziali</p>';
        };
        return GUICredenziali;
    }(WUX.WComponent));
    GUI.GUICredenziali = GUICredenziali;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var GUILog = (function (_super) {
        __extends(GUILog, _super);
        function GUILog(id) {
            return _super.call(this, id ? id : '*', 'GUILog') || this;
        }
        GUILog.prototype.render = function () {
            return '<p>GUILog</p>';
        };
        return GUILog;
    }(WUX.WComponent));
    GUI.GUILog = GUILog;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var GUISchedulatore = (function (_super) {
        __extends(GUISchedulatore, _super);
        function GUISchedulatore(id) {
            return _super.call(this, id ? id : '*', 'GUISchedulatore') || this;
        }
        GUISchedulatore.prototype.render = function () {
            return '<p>GUISchedulatore</p>';
        };
        return GUISchedulatore;
    }(WUX.WComponent));
    GUI.GUISchedulatore = GUISchedulatore;
})(GUI || (GUI = {}));
var GUI;
(function (GUI) {
    var GUISchedulazioni = (function (_super) {
        __extends(GUISchedulazioni, _super);
        function GUISchedulazioni(id) {
            return _super.call(this, id ? id : '*', 'GUISchedulazioni') || this;
        }
        GUISchedulazioni.prototype.render = function () {
            return '<p>GUISchedulazioni</p>';
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
                        var idx_1 = WUtil.indexOf(result, GUI.IServizio.sID_SERVIZIO, _this.selId);
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