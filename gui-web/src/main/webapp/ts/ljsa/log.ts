namespace GUI {

    export class GUILog extends WUX.WComponent {

        constructor(id?: string) {
            super(id ? id : '*', 'GUILog');
        }

        protected render() {
            return '<p>GUILog</p>'
        }

    }
}