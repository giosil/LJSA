namespace GUI {

    export class GUISchedulatore extends WUX.WComponent {

        constructor(id?: string) {
            super(id ? id : '*', 'GUISchedulatore');
        }

        protected render() {
            return '<p>GUISchedulatore</p>'
        }

    }
}