import {LitElement, html, css} from "https://unpkg.com/@polymer/lit-element@latest/lit-element.js?module";

class LetterElement extends LitElement {

    static get properties() {
        return {
            id: { type: Number },
            class: { type: String },
            cols: { type: Array },
        }
    }

    constructor() {
        super();
        this.letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
    }

    static get styles() {
        return [
            super.styles,
            css`
            .letter {
              display: inline-block;
              float: left;
              border-style: outset;
              border-width: 0.05em;
              width: 1.2em;
              height: 1.2em;
              text-align: center;
              background-color: #E0E0FF;
              border-color: #F6F6FF;
              border-top-color: black;
              margin-top: 0.05em;
            }
            `,
        ];
    }

    render() {
        return html`
            ${this.letters.map(letter => html`
                <span class="letter" id="letter-${letter}">${letter}</span>                
            `)}
            
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
        `;
    }

}

class NumberElement extends LitElement {
    static get properties() {
        return {
            class: { type: String },
            id: { type: Number},
            size: { type: Number},
        }
    }

    constructor() {
        super();
        this.size = 8;
    }

    static get styles() {
        return [
            super.styles,
            css`
            .number {
              display: inline-block;
              float: left;
              border-style: outset;
              border-width: 0.05em;
              width: 1.2em;
              height: 1.2em;
              text-align: center;
              background-color: #E0E0FF;
              border-color: #F6F6FF;
              margin-right: 0.05em;
            }
            `,
        ];
    }

    render() {
        return html`
            <span class="number">
                ${this.size - parseInt(this.id)}
            </span>
            
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
        `;
    }
}

customElements.define('letter-element', LetterElement);
customElements.define('number-element', NumberElement);
