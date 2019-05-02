import React, {useContext} from "react";
import {MainShutterContext} from "../context/MainShutterContext";
import PropTypes from "prop-types";
import {PosedLine} from "./internals/PosedLine";

export const Light = (props) => {
    const blocked = !useContext(MainShutterContext).open
    const poseKey = `${props.x}-${blocked ? props.initialWidth : "100"}`

    return <svg x={"0"} y="200">
        <defs>
            /*ARROW END*/
            <marker id="arrow"
                    markerWidth="4"
                    markerHeight="4"
                    refX={"0"}
                    refY={"2"}
                    orient={"auto"}
                    markerUnits={"strokeWidth"}>
                <polygon points="0,0 0,4 4,2" fill="red"/>
            </marker>
        </defs>
        /*LIGHT*/
        <PosedLine id={"light"}
                   poseKey={poseKey}
                   pose={'default'}
                   x1={props.x}
                   x2={blocked ? props.initialWidth : "100"}
                   y1="50"
                   y2="50"
                   stroke="red"
                   strokeWidth="5"
                   markerEnd="url(#arrow)"
        />
    </svg>
}

Light.propTypes = {
    x: PropTypes.number.isRequired,
    initialWidth: PropTypes.number.isRequired
};
