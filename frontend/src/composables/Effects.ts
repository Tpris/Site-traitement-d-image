export enum EffectTypes {
    Filter = "filter",
    GaussianBlur = "gaussianBlur",
    MeanBlur = "meanBlur",
    Luminosity = "luminosity",
    Sobel = "sobel",
    EgalisationS = "egalisationS",
    EgalisationV = "egalisationV"
}

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
    step: number
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

    function Cursors(text:string, name:string, param: string[], step: number){
        this.text = text;
        this.name = name;
        this.param = param;
        this.value = 0;
        this.step = step
    }

    function Effect(type:string) {
        this.type = type
        this.isActive = false

        switch(type){
            case EffectTypes.Filter:
                this.text = "Filtre de teinte"
                this.params = new Params(null, [
                    new Cursors("Teinte", "hue", ["0", "255"], 1),
                    new Cursors("min", "smin", ["0", "1"], 0.01),
                    new Cursors("max", "smax", ["0", "1"], 0.01)
                ] as ICursors[])
                break;

            case EffectTypes.GaussianBlur:
                this.text = "Filtre gaussien"
                this.params = new Params(
                    [new DropBox("Type", "BT",["Skip", "Normalized", "Extended", "Reflect"])] as IDropBox[],
                    [ new Cursors("Taille", "size", ["1", "255"],2),
                        new Cursors("Ecart type","sigma", ["0", "7"], 1)
                    ] as ICursors[])
                break;

            case EffectTypes.MeanBlur:
                this.text = "Filtre moyenneur"
                this.params = new Params(
                    [new DropBox("Type", "BT",["Skip", "Normalized", "Extended", "Reflect"])] as IDropBox[],
                    [new Cursors("Taille", "size", ["1", "255"],2)] as ICursors[]
                )
                break;

            case EffectTypes.Luminosity:
                this.text = "Luminosité"
                this.params = new Params(null, [new Cursors("Delta", "delta",["-255", "255"], 1)] as ICursors[])
                break

            case EffectTypes.Sobel:
                this.text = "Sobel"
                this.params = new Params(null, null)
                break;

            case EffectTypes.EgalisationS:
                this.text = "Egalisation S"
                this.params = new Params(null, null)
                break;

            case EffectTypes.EgalisationV:
                this.text = "Egalisation V"
                this.params = new Params(null, null)
                break;

            default:
                this.text = ""
                this.params = new Params(null, null)
                break;
        }
    }

    return {Effect}
}