export enum EffectTypes {
    Filter = "filter",
    Rainbow = "rainbow",
    GaussianBlur = "gaussianBlur",
    MeanBlur = "meanBlur",
    Luminosity = "luminosity",
    Sobel = "sobel",
    EgalisationS = "egalisationS",
    EgalisationV = "egalisationV",
    EgalisationRGB = "egalisationRGB",
    DynContrast = "dynamicContrast",
    Negative = "negativeImg",
    Threshold = "threshold",
    ColorToGray = "RGBtoGray",
    Draw = "draw",
    WaterColor = "waterColor",
    Tourbillon = "tourbillon",
    Fisheyes = "fisheyes",
    Rotation = "rotation"
}

export class Params{
    private readonly _dropBoxes: DropBox[];
    private readonly _cursors: Cursors[];
    constructor(dropBoxes: DropBox[] | null, cursors: Cursors[] | null) {
        if(dropBoxes === null) this._dropBoxes = Array<DropBox>();
        else this._dropBoxes = dropBoxes;
        if(cursors === null) this._cursors = Array<Cursors>();
        else this._cursors = cursors;
    }
    get cursors(): Cursors[] {
        return this._cursors;
    }
    get dropBoxes(): DropBox[] {
        return this._dropBoxes;
    }
}

class ItemEffect{
    protected readonly _name: string;
    protected readonly _param: string[];
    protected readonly _text: string;

    constructor(text:string, name:string, param: string[]) {
        this._text = text;
        this._name = name;
        this._param = param;
    }

    get name(): string {
        return this._name;
    }
    get text(): string {
        return this._text;
    }
    get param(): string[] {
        return this._param;
    }
}

export class DropBox extends ItemEffect{
    private _value: string;
    constructor(text:string, name:string, param: string[]) {
        super(text, name, param);
        this._value = "";
    }
    get value(): string {
        return this._value;
    }
    set value(value: string) {
        this._value = value;
    }
}

export class Cursors extends ItemEffect{
    private readonly _step: number;
    private _value: number;
    constructor(text:string, name:string, param: string[], step: number, value: number) {
        super(text, name, param);
        this._value = value;
        this._step = step
    }
    get value(): number {
        return this._value;
    }
    get step(): number {
        return this._step;
    }
    set value(value: number) {
        this._value = value;
    }
}

export class Effect {
    private readonly _type: string;
    private readonly _isActive: boolean;
    private readonly _text: string;
    private readonly _params: Params;
    constructor(type:string){
        this._type = type
        this._isActive = false

        switch(type) {
            case EffectTypes.Filter:
                this._text = "Teinte"
                this._params = new Params(null, [
                    new Cursors("Teinte", "hue", ["0", "359"], 1, 0),
                    new Cursors("min", "smin", ["0", "1"], 0.01, 0),
                    new Cursors("max", "smax", ["0", "1"], 0.01, 1)
                ] as Cursors[])
                break;

            case EffectTypes.Rainbow:
                this._text = "Rainbow"
                this._params = new Params(null, [
                    new Cursors("min", "smin", ["0", "1"], 0.01, 0),
                    new Cursors("max", "smax", ["0", "1"], 0.01, 1)
                ] as Cursors[])
                break;
            
            case EffectTypes.DynContrast:
                this._text = "Dyn. Contrast"
                this._params = new Params(null, [
                    new Cursors("min", "min", ["0", "255"], 1, 0),
                    new Cursors("max", "max", ["0", "255"], 1, 255)
                ] as Cursors[])
                break;

            case EffectTypes.Threshold:
                this._text = "Threshold"
                this._params = new Params(null, [
                    new Cursors("threshold", "threshold", ["0", "255"], 1, 122)
                ] as Cursors[])
                break;

            case EffectTypes.GaussianBlur:
                this._text = "Gauss."
                this._params = new Params(
                    [new DropBox("Type", "BT", ["SKIP", "NORMALIZED", "EXTENDED", "REFLECT"])] as DropBox[],
                    [new Cursors("Taille", "size", ["1", "35"], 2, 1),
                        new Cursors("Ecart type", "sigma", ["1", "7"], 1, 1)
                    ] as Cursors[])
                break;

            case EffectTypes.MeanBlur:
                this._text = "Flou"
                this._params = new Params(
                    [new DropBox("Type", "BT", ["SKIP", "NORMALIZED", "EXTENDED", "REFLECT"])] as DropBox[],
                    [new Cursors("Taille", "size", ["1", "35"], 2, 1)] as Cursors[]
                )
                break;

            case EffectTypes.Luminosity:
                this._text = "Lum."
                this._params = new Params(null, [new Cursors("Delta", "delta", ["-255", "255"], 1, 0)] as Cursors[])
                break

            case EffectTypes.Fisheyes:
                this._text = "Fish eyes"
                this._params = new Params(
                    [new DropBox("Perspective", "persp", ["TOPLEFT", "TOP", "TOPRIGHT",
                        "LEFT", "CENTER", "RIGHT","BOTTOMLEFT", "BOTTOM", "BOTTOMRIGHT"])] as DropBox[], 
                    [new Cursors("Delta", "delta", ["0", "0.01"], 0.001, 0)] as Cursors[])
                break

            case EffectTypes.Rotation:
                this._text = "Rot°"
                this._params = new Params(null, [new Cursors("Angle", "theta", ["0", "360"], 0.1, 0)] as Cursors[])
                break

            case EffectTypes.Tourbillon:
                this._text = "Tourbillon"
                this._params = new Params(null, [new Cursors("Rotation", "tourbillon", ["0.001", "0.15"], 0.001, 0.001)] as Cursors[])
                break

            case EffectTypes.Sobel:
                this._text = "Sobel"
                this._params = new Params(
                    [new DropBox("Type", "color", ["Color","White"])] as DropBox[],
                    null)
                break;

            case EffectTypes.EgalisationS:
                this._text = "Egal° S"
                this._params = new Params(null, null)
                break;

            case EffectTypes.EgalisationV:
                this._text = "Egal° V"
                this._params = new Params(null, null)
                break;

            case EffectTypes.EgalisationRGB:
                this._text = "Egal° RGB"
                this._params = new Params(null, null)
                break;

            case EffectTypes.Negative:
                this._text = "Neg."
                this._params = new Params(null, null)
                break;

            case EffectTypes.ColorToGray:
                this._text = "Gray"
                this._params = new Params(null, null)
                break;

            case EffectTypes.WaterColor:
                this._text = "Water Color"
                this._params = new Params(null, null)
                break;
            
            case EffectTypes.Draw:
                this._text = "Draw"
                this._params = new Params(null, [
                    new Cursors("step", "step", ["1", "255"], 1, 1)
                ] as Cursors[])
                break;

            default:
                this._text = ""
                this._params = new Params(null, null)
                break;
        }
    }
    get text(): string {
        return this._text;
    }
    get params(): Params {
        return this._params;
    }
    get type(): string {
        return this._type;
    }
}