import Client from "./Client";

class AssemblyCommandWebClient extends Client {
    protected baseUrl: string;

    constructor(baseUrl: string) {
        super(baseUrl)
    }

    public submit(url:string, input: string, callback: (res: string) => void) {
        this.post(`${this.baseUrl}${url}Submit`, callback, input)
    }
}

export default AssemblyCommandWebClient