namespace GUI {

    export class GUIAttivita extends WUX.WComponent {

        constructor(id?: string) {
            super(id ? id : '*', 'GUIAttivita');
        }

        protected render() {
            return '<p>GUIAttivita</p>'
        }

    }
}