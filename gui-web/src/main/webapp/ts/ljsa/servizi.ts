namespace GUI {

    export class GUIServizi extends WUX.WComponent {

        constructor(id?: string) {
            super(id ? id : '*', 'GUIServizi');
        }

        protected render() {
            return '<p>GUIServizi</p>'
        }

    }
}