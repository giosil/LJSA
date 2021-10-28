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
    var GUIServizi = (function (_super) {
        __extends(GUIServizi, _super);
        function GUIServizi(id) {
            return _super.call(this, id ? id : '*', 'GUIServizi') || this;
        }
        GUIServizi.prototype.render = function () {
            return '<p>GUIServizi</p>';
        };
        return GUIServizi;
    }(WUX.WComponent));
    GUI.GUIServizi = GUIServizi;
})(GUI || (GUI = {}));
//# sourceMappingURL=ljsa.js.map