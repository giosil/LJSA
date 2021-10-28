WUX.RES.OK = 'OK';
WUX.RES.CLOSE = 'Chiudi';
WUX.RES.CANCEL = 'Annulla';
WUX.RES.ERR_DATE = 'Data non ammessa.';
WUX.RES.FILE_NAME = 'File';
WUX.RES.FILE_SIZE = 'Dim.';
WUX.RES.FILE_TYPE = 'Tipo';
WUX.RES.FILE_LMOD = 'Ult.Mod.';
var GUI;
(function (GUI) {
    var WIcon = WUX.WIcon;
    var TXT = (function () {
        function TXT() {
        }
        TXT.OK = 'OK';
        TXT.CLOSE = 'Chiudi';
        TXT.NEW = 'Nuovo';
        TXT.OPEN = 'Modifica';
        TXT.DELETE = 'Elimina';
        TXT.SAVE = 'Salva';
        TXT.SEND = 'Invia';
        TXT.SEND_EMAIL = 'Email';
        TXT.FIND = 'Cerca';
        TXT.FORCE = 'Forza';
        TXT.SEARCH = 'Cerca';
        TXT.CANCEL = 'Annulla';
        TXT.RESET = 'Annulla';
        TXT.PRINT = 'Stampa';
        TXT.PRINT_ALL = 'Stampa Tutto';
        TXT.PREVIEW = 'Anteprima';
        TXT.EXPORT = 'Esporta';
        TXT.IMPORT = 'Importa';
        TXT.HELP = 'Guida';
        TXT.VIEW = 'Vedi';
        TXT.ENABLE = 'Abilita';
        TXT.DISABLE = 'Disabilita';
        TXT.ADD = 'Aggiungi';
        TXT.APPLY = 'Applica';
        TXT.REMOVE = 'Rimuovi';
        TXT.REMOVE_ALL = 'Rim.Tutto';
        TXT.REFRESH = 'Aggiorna';
        TXT.UNDO = 'Annulla';
        TXT.SETTINGS = 'Impostazioni';
        TXT.COPY = 'Copia';
        TXT.CUT = 'Taglia';
        TXT.PASTE = 'Incolla';
        TXT.CONFIRM = 'Conferma';
        TXT.FORWARD = 'Avanti';
        TXT.BACKWARD = 'Indietro';
        TXT.NEXT = 'Prossimo';
        TXT.PREVIOUS = 'Precedente';
        TXT.SELECT = 'Seleziona';
        TXT.SELECT_ALL = 'Sel.Tutto';
        TXT.WORK = 'Lavora';
        TXT.AGGREGATE = 'Aggrega';
        TXT.SET = 'Imposta';
        TXT.DEFAULT = 'Predefinito';
        TXT.REWORK = 'Rielabora';
        TXT.PUSH = 'Spedisci';
        TXT.SUSPEND = 'Sospendi';
        TXT.RESUME = 'Riattiva';
        TXT.CODE = 'Codice';
        TXT.DESCRIPTION = 'Descrizione';
        TXT.GROUP = 'Gruppo';
        TXT.ROLE = 'Ruolo';
        TXT.TYPE = 'Tipo';
        TXT.HELLO = 'Ciao';
        return TXT;
    }());
    GUI.TXT = TXT;
    var MSG = (function () {
        function MSG() {
        }
        MSG.CONF_DELETE = 'Eliminare l\'elemento selezionato?';
        MSG.CONF_DISABLE = 'Disabilitare l\'elemento selezionato?';
        MSG.CONF_ENABLE = 'Abilitare l\'elemento selezionato?';
        MSG.CONF_CANCEL = 'Si vogliono annullare le modifiche apportate?';
        MSG.CONF_PROCEED = 'Si vuole procedere con l\'operazione?';
        MSG.CONF_OVERWRITE = 'Si vuole procedere con la sovrascrittura?';
        MSG.MSG_COMPLETED = 'Operazione completata con successo.';
        MSG.MSG_ERRORS = 'Errore durante l\'elaborazione.';
        return MSG;
    }());
    GUI.MSG = MSG;
    var ICO = (function () {
        function ICO() {
        }
        ICO.TRUE = WIcon.CHECK_SQUARE_O;
        ICO.FALSE = WIcon.SQUARE_O;
        ICO.CLOSE = WIcon.TIMES;
        ICO.OK = WIcon.CHECK;
        ICO.CALENDAR = WIcon.CALENDAR;
        ICO.AGGREGATE = WIcon.CHAIN;
        ICO.NEW = WIcon.PLUS_SQUARE_O;
        ICO.EDIT = WIcon.EDIT;
        ICO.OPEN = WIcon.EDIT;
        ICO.DELETE = WIcon.TRASH;
        ICO.DETAIL = WIcon.FILE_TEXT_O;
        ICO.SAVE = WIcon.CHECK;
        ICO.FIND = WIcon.SEARCH;
        ICO.FIND_DIFF = WIcon.SEARCH_MINUS;
        ICO.FIND_PLUS = WIcon.SEARCH_PLUS;
        ICO.FORCE = WIcon.CHECK_CIRCLE;
        ICO.FORCE_ALL = WIcon.CHECK_CIRCLE_O;
        ICO.SEARCH = WIcon.SEARCH;
        ICO.CANCEL = WIcon.UNDO;
        ICO.RESET = WIcon.TIMES_CIRCLE;
        ICO.PRINT = WIcon.PRINT;
        ICO.PREVIEW = WIcon.SEARCH_PLUS;
        ICO.EXPORT = WIcon.SHARE_SQUARE_O;
        ICO.IMPORT = WIcon.SIGN_IN;
        ICO.FILE = WIcon.FILE_O;
        ICO.HELP = WIcon.QUESTION_CIRCLE;
        ICO.VIEW = WIcon.FILE_TEXT_O;
        ICO.ENABLE = WIcon.THUMBS_O_UP;
        ICO.DISABLE = WIcon.THUMBS_O_DOWN;
        ICO.ADD = WIcon.PLUS;
        ICO.APPLY = WIcon.CHECK;
        ICO.REMOVE = WIcon.MINUS;
        ICO.REFRESH = WIcon.REFRESH;
        ICO.UNDO = WIcon.UNDO;
        ICO.SETTINGS = WIcon.COG;
        ICO.OPTIONS = WIcon.CHECK_SQUARE;
        ICO.PASSWORD = WIcon.UNDO;
        ICO.COPY = WIcon.COPY;
        ICO.CUT = WIcon.CUT;
        ICO.PASTE = WIcon.PASTE;
        ICO.FORWARD = WIcon.ANGLE_DOUBLE_RIGHT;
        ICO.BACKWARD = WIcon.ANGLE_DOUBLE_LEFT;
        ICO.NEXT = WIcon.FORWARD;
        ICO.PREVIOUS = WIcon.BACKWARD;
        ICO.CONFIRM = WIcon.CHECK;
        ICO.FILTER = WIcon.FILTER;
        ICO.SEND = WIcon.SEND;
        ICO.SEND_EMAIL = WIcon.ENVELOPE_O;
        ICO.WAIT = WIcon.COG;
        ICO.WORK = WIcon.COG;
        ICO.CONFIG = WIcon.COG;
        ICO.LEFT = WIcon.ARROW_CIRCLE_LEFT;
        ICO.RIGHT = WIcon.ARROW_CIRCLE_RIGHT;
        ICO.SELECT_ALL = WIcon.TH_LIST;
        ICO.REWORK = WIcon.REFRESH;
        ICO.PUSH = WIcon.TRUCK;
        ICO.AHEAD = WIcon.ANGLE_DOUBLE_RIGHT;
        ICO.SUSPEND = WIcon.TOGGLE_OFF;
        ICO.RESUME = WIcon.RECYCLE;
        ICO.PAIRING = WIcon.RANDOM;
        ICO.CHECK = WIcon.CHECK_SQUARE_O;
        ICO.EVENT = WIcon.BOLT;
        ICO.MESSAGE = WIcon.ENVELOPE_O;
        ICO.USER = WIcon.USER_O;
        ICO.GROUP = WIcon.USERS;
        ICO.TOOL = WIcon.WRENCH;
        ICO.DEMOGRAPHIC = WIcon.ADDRESS_CARD;
        ICO.DOCUMENT = WIcon.FILE_TEXT_O;
        ICO.LINKS = WIcon.CHAIN;
        ICO.WARNING = WIcon.WARNING;
        ICO.INFO = WIcon.INFO_CIRCLE;
        ICO.CRITICAL = WIcon.TIMES_CIRCLE;
        return ICO;
    }());
    GUI.ICO = ICO;
})(GUI || (GUI = {}));
//# sourceMappingURL=res-it.js.map