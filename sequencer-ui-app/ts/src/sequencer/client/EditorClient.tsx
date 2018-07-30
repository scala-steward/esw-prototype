import Client from "./Client";

class EditorClient extends Client {

    constructor(baseUrl: string) {
        super(baseUrl)
    }

    public pause(callback: (res: string) => void) {
        this.post(`${this.baseUrl}/editor/pause`, callback)
    };

    public resume(callback: (res: string) => void) {
        this.post(`${this.baseUrl}/editor/resume`, callback)
    };

    public showSequence(callback: (res: string) => void) {
        this.post(`${this.baseUrl}/editor/sequence`, callback)
    }
}

export default EditorClient;
