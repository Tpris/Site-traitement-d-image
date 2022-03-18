export interface IEffect{
    type: string
    text: string
    params: IParams
}

export interface IParams {
    dropBoxes:IDropBox[]
    cursors:ICursors[]
}

export interface IDropBox{
    text: string
    name: string
    param:string[]
    value: string
}

export interface ICursors{
    text: string
    name: string
    param: string[]
    value: number
}

export default function useEffects() {
    function Params(dropBoxes: IDropBox[] | null, cursors: ICursors[] | null){
        if(dropBoxes === null) this.dropBoxes = Array<IDropBox>()
        else this.dropBoxes = dropBoxes

        if(cursors === null) this.cursors = Array<ICursors>()
        else this.cursors = cursors
    }

    function DropBox(text:string, name:string, param: string[]){
        this.text = text;
        this.name = name;
        this.param = param;
        this.value = "";
    }

    function Cursors(text:string, name:string, param: string[]){
        this.text = text;
        this.name = name;
        this.param = param;
        this.value = 0;
    }

    function Effect(type:string) {
        this.type = type
        this.isActive = false

        switch(type){
            case "filter":
                this.text = "Filtre de teinte"
                this.params = new Params(null, [
                    new Cursors("Teinte", "hue", ["0", "255"]),
                    new Cursors("min", "smin", ["0", "255"]),
                    new Cursors("max", "smax", ["0", "255"])
                ] as ICursors[])
                break;

            case "gaussianBlur":
                this.text = "Filtre gaussien"
                this.params = new Params(
                    [new DropBox("Type", "BT",["Skip", "Normalized", "Extended", "Reflect"])] as IDropBox[],
                    [ new Cursors("Taille", "size", ["0", "255"]),
                        new Cursors("Ecart type","sigma", ["0", "255"])
                    ] as ICursors[])
                break;

            case "meanBlur":
                this.text = "Filtre moyenneur"
                this.params = new Params(
                    [new DropBox("Type", "BT",["Skip", "Normalized", "Extended", "Reflect"])] as IDropBox[],
                    [new Cursors("Taille", "size", ["0", "255"])] as ICursors[]
                )
                break;

            case "luminosity":
                this.text = "Luminosité"
                this.params = new Params(null, [new Cursors("Delta", "delta",["-255", "255"])] as ICursors[])
                break

            case "sobel":
                this.text = "Sobel"
                this.params = new Params(null, null)
                break;

            case "egalisation":
                this.text = "Egalisation"
                this.params = new Params([new DropBox("HSV", "SV", ["S", "V"])] as IDropBox[], null)
                break;

            default:
                this.text = ""
                this.params = new Params(null, null)
                break;
        }
    }

    return {Effect}
}