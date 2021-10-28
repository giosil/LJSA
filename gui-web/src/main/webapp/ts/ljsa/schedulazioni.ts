namespace GUI {

    export class GUISchedulazioni extends WUX.WComponent {

        constructor(id?: string) {
            super(id ? id : '*', 'GUISchedulazioni');
        }

        protected render() {
            return '<p>GUISchedulazioni</p>'
        }

    }
}