WUX.RES.OK = 'OK';
WUX.RES.CLOSE = 'Cerca';
WUX.RES.CANCEL = 'Cancelar';
WUX.RES.ERR_DATE = 'Fecha invalida.';
WUX.RES.FILE_NAME = 'File';
WUX.RES.FILE_SIZE = 'Dim.';
WUX.RES.FILE_TYPE = 'Tipo';
WUX.RES.FILE_LMOD = 'Últ.Mod.';

namespace GUI {
    import WIcon = WUX.WIcon;

    export class TXT {
        // Captions
        static readonly OK = 'OK';
        static readonly CLOSE = 'Cerca';
        static readonly NEW = 'Nuevo';
        static readonly OPEN = 'Modificar';
        static readonly DELETE = 'Eliminar';
        static readonly SAVE = 'Salvar';
        static readonly SEND = 'Enviar';
        static readonly SEND_EMAIL = 'Email';
        static readonly FIND = 'Encontrar';
        static readonly FORCE = 'Fuerza';
        static readonly SEARCH = 'Buscar';
        static readonly CANCEL = 'Cancelar';
        static readonly RESET = 'Reiniciar';
        static readonly PRINT = 'Impresión';
        static readonly PRINT_ALL = 'Imprimir todo';
        static readonly PREVIEW = 'Avance';
        static readonly EXPORT = 'Exportar';
        static readonly IMPORT = 'Importar';
        static readonly HELP = 'Ayuda';
        static readonly VIEW = 'Ver';
        static readonly ENABLE = 'Habilitar';
        static readonly DISABLE = 'Inhabilitar';
        static readonly ADD = 'Añadir';
        static readonly APPLY = 'Aplicar';
        static readonly REMOVE = 'Eliminar';
        static readonly REMOVE_ALL = 'El.todo';
        static readonly REFRESH = 'Actualizar';
        static readonly UNDO = 'Deshacer';
        static readonly SETTINGS = 'Configuraciones';
        static readonly COPY = 'Copiar';
        static readonly CUT = 'Cortar';
        static readonly PASTE = 'Pegar';
        static readonly CONFIRM = 'Confirmar';
        static readonly FORWARD = 'Adelante';
        static readonly BACKWARD = 'Hacia atrás';
        static readonly NEXT = 'Próximo';
        static readonly PREVIOUS = 'Previo';
        static readonly SELECT = 'Seleccione';
        static readonly SELECT_ALL = 'Sel.todo';
        static readonly WORK = 'Trabajar';
        static readonly AGGREGATE = 'Aggrega';
        static readonly SET = 'Establecer';
        static readonly DEFAULT = 'Default';
        static readonly REWORK = 'Rehacer';
        static readonly PUSH = 'Empujar';
        static readonly SUSPEND = 'Suspender';
        static readonly RESUME = 'Reasumir';
        // Entities
        static readonly CODE = 'Código';
        static readonly DESCRIPTION = 'Descripción';
        static readonly GROUP = 'Grupo';
        static readonly ROLE = 'Rol';
        static readonly TYPE = 'Tipo';
        // APP
        static readonly HELLO = 'Hola';
    }

    export class MSG {
        static readonly CONF_DELETE = '¿Quieres eliminar el elemento seleccionado?';
        static readonly CONF_DISABLE = '¿Quieres deshabilitar el elemento seleccionado?';
        static readonly CONF_ENABLE = '¿Quieres habilitar el elemento seleccionado?';
        static readonly CONF_CANCEL = '¿Quieres deshacer los cambios?';
        static readonly CONF_PROCEED = '¿Quieres continuar con la operación?';
        static readonly CONF_OVERWRITE = '¿Quiere continuar con la sobrescritura?';

        static readonly MSG_COMPLETED = 'La operación se realizó con éxito.';
        static readonly MSG_ERRORS = 'Error durante el procesamiento.';
    }

    export class ICO {
        // base
        static readonly TRUE = WIcon.CHECK_SQUARE_O;
        static readonly FALSE = WIcon.SQUARE_O;
        static readonly CLOSE = WIcon.TIMES;
        static readonly OK = WIcon.CHECK;
        static readonly CALENDAR = WIcon.CALENDAR;
        // common form tools
        static readonly AGGREGATE = WIcon.CHAIN;
        static readonly NEW = WIcon.PLUS_SQUARE_O;
        static readonly EDIT = WIcon.EDIT;
        static readonly OPEN = WIcon.EDIT;
        static readonly DELETE = WIcon.TRASH;
        static readonly DETAIL = WIcon.FILE_TEXT_O;
        static readonly SAVE = WIcon.CHECK;
        static readonly FIND = WIcon.SEARCH;
        static readonly FIND_DIFF = WIcon.SEARCH_MINUS;
        static readonly FIND_PLUS = WIcon.SEARCH_PLUS;
        static readonly FORCE = WIcon.CHECK_CIRCLE;
        static readonly FORCE_ALL = WIcon.CHECK_CIRCLE_O;
        static readonly SEARCH = WIcon.SEARCH;
        static readonly CANCEL = WIcon.UNDO;
        static readonly RESET = WIcon.TIMES_CIRCLE;
        static readonly PRINT = WIcon.PRINT;
        static readonly PREVIEW = WIcon.SEARCH_PLUS;
        static readonly EXPORT = WIcon.SHARE_SQUARE_O;
        static readonly IMPORT = WIcon.SIGN_IN;
        static readonly FILE = WIcon.FILE_O;
        static readonly HELP = WIcon.QUESTION_CIRCLE;
        static readonly VIEW = WIcon.FILE_TEXT_O;
        static readonly ENABLE = WIcon.THUMBS_O_UP;
        static readonly DISABLE = WIcon.THUMBS_O_DOWN;
        static readonly ADD = WIcon.PLUS;
        static readonly APPLY = WIcon.CHECK;
        static readonly REMOVE = WIcon.MINUS;
        static readonly REFRESH = WIcon.REFRESH;
        static readonly UNDO = WIcon.UNDO;
        static readonly SETTINGS = WIcon.COG;
        static readonly OPTIONS = WIcon.CHECK_SQUARE;
        static readonly PASSWORD = WIcon.UNDO;
        static readonly COPY = WIcon.COPY;
        static readonly CUT = WIcon.CUT;
        static readonly PASTE = WIcon.PASTE;
        static readonly FORWARD = WIcon.ANGLE_DOUBLE_RIGHT;
        static readonly BACKWARD = WIcon.ANGLE_DOUBLE_LEFT;
        static readonly NEXT = WIcon.FORWARD;
        static readonly PREVIOUS = WIcon.BACKWARD;
        static readonly CONFIRM = WIcon.CHECK;
        static readonly FILTER = WIcon.FILTER;
        static readonly SEND = WIcon.SEND;
        static readonly SEND_EMAIL = WIcon.ENVELOPE_O;
        static readonly WAIT = WIcon.COG;
        static readonly WORK = WIcon.COG;
        static readonly CONFIG = WIcon.COG;
        static readonly LEFT = WIcon.ARROW_CIRCLE_LEFT;
        static readonly RIGHT = WIcon.ARROW_CIRCLE_RIGHT;
        static readonly SELECT_ALL = WIcon.TH_LIST;
        static readonly REWORK = WIcon.REFRESH;
        static readonly PUSH = WIcon.TRUCK;
        static readonly AHEAD = WIcon.ANGLE_DOUBLE_RIGHT;
        static readonly SUSPEND = WIcon.TOGGLE_OFF;
        static readonly RESUME = WIcon.RECYCLE;
        static readonly PAIRING = WIcon.RANDOM;
        static readonly CHECK = WIcon.CHECK_SQUARE_O;
        // common entities
        static readonly EVENT = WIcon.BOLT;
        static readonly MESSAGE = WIcon.ENVELOPE_O;
        static readonly USER = WIcon.USER_O;
        static readonly GROUP = WIcon.USERS;
        static readonly TOOL = WIcon.WRENCH;
        static readonly DEMOGRAPHIC = WIcon.ADDRESS_CARD;
        static readonly DOCUMENT = WIcon.FILE_TEXT_O;
        static readonly LINKS = WIcon.CHAIN;
        // messages
        static readonly WARNING = WIcon.WARNING;
        static readonly INFO = WIcon.INFO_CIRCLE;
        static readonly CRITICAL = WIcon.TIMES_CIRCLE;
    }

}