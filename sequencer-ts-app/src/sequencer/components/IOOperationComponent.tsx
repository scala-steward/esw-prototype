import * as React from 'react';
import {Component} from 'react';
import {cssClasses} from "../jss/customstyles";

interface IProps {
    componentNameProp: string
    operation: string
    output: string,
    feedApi(input: string): void
}

interface IState {
    input: string
}


class IOOperationComponent extends Component<IProps, IState> {

    constructor(props: IProps) {
        super(props);
        this.updateInput = this.updateInput.bind(this);
        this.handleClick = this.handleClick.bind(this);

        this.state = {input: ""};
    }

    public render() {
        const {
            componentNameProp,
            operation,
            output,
        } = this.props;

        return (
            <div className="card-panel hoverable">
                <h6>{componentNameProp} Request</h6>
                <div><span><textarea className={`${cssClasses.textArea}`} value={this.state.input} onChange={this.updateInput}/></span></div>
                <div>
                    <button className={`${cssClasses.button}`} onClick={this.handleClick}>{operation}</button>
                </div>
                <div><span><textarea className={`${cssClasses.textArea}`} value={output}/></span></div>
            </div>
        );
    }

    private updateInput(event: React.ChangeEvent<HTMLTextAreaElement>) {
        this.setState({
            input: event.target.value
        })
    };

    private handleClick(event: React.MouseEvent<HTMLButtonElement>) {
        this.props.feedApi(this.state.input)
    };
}

export {IOOperationComponent}
