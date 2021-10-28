namespace GUI {

    export class GUIClassi extends WUX.WComponent {

        constructor(id?: string) {
            super(id ? id : '*', 'GUIClassi');
        }

        protected render() {
            return '<p>GUIClassi</p>'
        }

    }
}