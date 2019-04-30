import React, {useContext} from "react";
import {TextBox} from "./common/TextBox";
import {DashBox} from "./common/DashBox";
import {Mirror} from "./common/Mirror";
import {InstrumentCalibrationMirrorContext} from "../context/InstrumentCalibrationMirrorContext";
import PropTypes from "prop-types";

export const CalibrationUnit = (props) => {
    const {isUp, toggleMirror} = useContext(InstrumentCalibrationMirrorContext)
    const width = props.width;
    const strokeWidth = 2;
    const height = 500;

    const mirrorMidY = isUp ? height/2 : height/2 + height/6

    return <svg x={props.x}>
        <DashBox backgroundColor="#00feff" x={2} y={2} width={width} height={height}>
            <TextBox x={width * 0.1 + strokeWidth} y={height * 0.03} width={width * 0.8} height={height * 0.12}>
                NFIRAOS Science Calibration Unit
            </TextBox>
            <TextBox width={width * 0.8}
                     height={height * 0.12}
                     y={height * 0.18}
                     x={width * 0.1 + strokeWidth}
                     color="white"
                     backgroundColor="#0433FF">
                NSCU arcs & flats
            </TextBox>
            <Mirror midX={width/2} midY={mirrorMidY} onClick={toggleMirror} />
            <TextBox width={width * 0.8} height={height * 0.12} y={height * 0.80} x={width * 0.1 + strokeWidth}>
                Instrument Calibration Mirror
            </TextBox>
        </DashBox>
    </svg>
}

CalibrationUnit.propTypes = {
    x: PropTypes.number.isRequired,
    width: PropTypes.number.isRequired
};
