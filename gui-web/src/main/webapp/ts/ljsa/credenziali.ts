namespace GUI {

    export class GUICredenziali extends WUX.WComponent {

        constructor(id?: string) {
            super(id ? id : '*', 'GUICredenziali');
        }

        protected render() {
            return '<p>GUICredenziali</p>'
        }

    }
}