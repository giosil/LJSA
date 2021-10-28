WUX.RES.OK = 'OK';
WUX.RES.CLOSE = 'Fermez';
WUX.RES.CANCEL = 'Annulez';
WUX.RES.ERR_DATE = 'Date non autorisée.';
WUX.RES.FILE_NAME = 'Fichier';
WUX.RES.FILE_SIZE = 'Dim.';
WUX.RES.FILE_TYPE = 'Type';
WUX.RES.FILE_LMOD = 'Der.Mod.';

namespace GUI {
    import WIcon = WUX.WIcon;

    export class TXT {
        // Captions
        static readonly OK = 'OK';
        static readonly CLOSE = 'Fermez';
        static readonly NEW = 'Nouvel';
        static readonly OPEN = 'Modifier';
        static readonly DELETE = 'Effacer';
        static readonly SAVE = 'Enregistrez';
        static readonly SEND = 'Soumettez';
        static readonly SEND_EMAIL = 'Email';
        static readonly FIND = 'Recherchez';
        static readonly FORCE = 'Forcer';
        static readonly SEARCH = 'Recherchez';
        static readonly CANCEL = 'Annulez';
        static readonly RESET = 'Annulez';
        static readonly PRINT = 'Imprimez';
        static readonly PRINT_ALL = 'Tout imprimer';
        static readonly PREVIEW = 'Aperçu';
        static readonly EXPORT = 'Exportation';
        static readonly IMPORT = 'Importer';
        static readonly HELP = 'Aide';
        static readonly VIEW = 'Voir';
        static readonly ENABLE = 'Activer';
        static readonly DISABLE = 'Désactiver';
        static readonly ADD = 'Ajouter';
        static readonly APPLY = 'Appliquer';
        static readonly REMOVE = 'Retirer';
        static readonly REMOVE_ALL = 'Tout supprimer';
        static readonly REFRESH = 'Rafraîchir';
        static readonly UNDO = 'Annuler';
        static readonly SETTINGS = 'Réglages';
        static readonly COPY = 'Copiez';
        static readonly CUT = 'Couper';
        static readonly PASTE = 'Coller';
        static readonly CONFIRM = 'Confirmez';
        static readonly FORWARD = 'Suivant';
        static readonly BACKWARD = 'En arrière';
        static readonly NEXT = 'Le prochain';
        static readonly PREVIOUS = 'Le précédent';
        static readonly SELECT = 'Sélectionner';
        static readonly SELECT_ALL = 'Tout sél.';
        static readonly WORK = 'Travailler';
        static readonly AGGREGATE = 'Agrégat';
        static readonly SET = 'Configurer';
        static readonly DEFAULT = 'Default';
        static readonly REWORK = 'Retravailler';
        static readonly PUSH = 'Soumettre';
        static readonly SUSPEND = 'Suspendre';
        static readonly RESUME = 'Restaurer';
        // Entities
        static readonly CODE = 'Code';
        static readonly DESCRIPTION = 'Description';
        static readonly GROUP = 'Groupe';
        static readonly ROLE = 'Rôle';
        static readonly TYPE = 'Type';
        // APP
        static readonly HELLO = 'Salut';
    }

    export class MSG {
        static readonly CONF_DELETE = 'Voulez-vous supprimer l\'élément sélectionné?';
        static readonly CONF_DISABLE = 'Voulez-vous désactiver l\'élément sélectionné?';
        static readonly CONF_ENABLE = 'Voulez-vous activer l\'élément sélectionné?';
        static readonly CONF_CANCEL = 'Voulez-vous annuler les modifications?';
        static readonly CONF_PROCEED = 'Voulez-vous continuer l\'opération?';
        static readonly CONF_OVERWRITE = 'Voulez-vous procéder à l\'écrasement?';

        static readonly MSG_COMPLETED = 'Opération terminée avec succès.';
        static readonly MSG_ERRORS = 'Erreur lors du traitement.';
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