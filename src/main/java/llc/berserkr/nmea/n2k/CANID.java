package llc.berserkr.nmea.n2k;

import llc.berserkr.nmea.n2k.util.ByteUtils;

public class CANID {

    private static byte PRIORITY_AND =  ByteUtils.bitStringToUnsignedByteArray("11100000")[0];//(byte) 0xE0;
    private static byte RESERVED_AND = ByteUtils.bitStringToUnsignedByteArray("00010000")[0];//(byte) 0x10;
    private static byte DATAPAGE_AND = ByteUtils.bitStringToUnsignedByteArray("00001000")[0];//(byte) 0x08;
    private static byte PDU_SHIFT_AND = ByteUtils.bitStringToUnsignedByteArray("00011111")[0];//(byte) 0x1F;

    private final byte priority;
    private final boolean reserved;
    private final boolean datapage;
    private final byte pduFormat;
    private final byte sourceAddress;

    /*

    ------------- 3 bits for priority
    1/28
    2/27
    3/26
    ------------- 18 bits for PGN
    4/25 * Reserved ( 1 bit )
    5/24 * Data Page ( 1 bit )
    6/23 * PDU Format (PF) ( 8 bits ) (if PF < 240 message is PDU1 which means it's addressable message PS contains destination address )
    7/22 ( if PF is >= 240 then message is a PDU2 which means it is a broadcast message so the PS contains the group extension
    8/21
    9/20
    10/19
    11/18
    12/17
    13/16
    14/15 * PDU Specific (PS) ( 8 bit )
    15/14
    16/13
    17/12
    18/11
    19/10
    20/9
    21/8
    -------------
    22/7 - Source address
    23/6
    24/5
    25/4
    26/3
    27/2
    28/1
    29/0

     */

    public CANID(final byte[] rawPacket) {

        this.priority = (byte)(rawPacket[0] & PRIORITY_AND);
        this.reserved = (rawPacket[0] & RESERVED_AND) == 16;
        this.datapage = (rawPacket[0] & DATAPAGE_AND) == 8;

        {
            //after shift we are using the left 3 bits, everything else is zero
            int shifted = (rawPacket[0] << 5) & PRIORITY_AND; // & 0XE0 makes everything right of the 3rd bit zero

            int shiftedRight = (rawPacket[1] >> 3) & PDU_SHIFT_AND;

            //take left 3 bites from shifted, right 5 bits from shiftedRight and make the pdu value
            this.pduFormat = (byte)(shifted | shiftedRight);

        }

        {

            int shiftedLeft = (rawPacket[1] << 5) & PRIORITY_AND; //re-using priority shift

            int shiftedRight = (rawPacket[2] >>> 3) & PDU_SHIFT_AND; //re-using pdu shift

            this.sourceAddress = (byte)(shiftedLeft | shiftedRight);

        }


    }

    public boolean getReserved() {
        return reserved;
    }

    public boolean getDatapage() {
        return datapage;
    }

    public int getPduFormat() {
        return pduFormat;
    }

    public int getSourceAddress() {
        return sourceAddress;
    }
    public byte getPriority() {
        return priority;
    }

    public static int ISO_ACK = 0xE800;//059392;//
//    1 Control Byte
// .  2 Group Function Value
// .  3 NMEA Reserved
// .  4 PGN of Requested Information

    public static int ISO_REQUEST = 0xE800; //059904
//    PGN being requested

    public static int ISO__DATA_TRANSPORT = 0xE800;//060160;//
//    1 Sequence number of multi-packet frame
// .  2 Multi-packet packetized data

    public static int ISO_CONNECTION_RTS = 0xE800;//060416;//
//1 RTS Group Function Code
//2 Total message size, bytes
//3 Total number of frames to be transmitted
//4 NMEA Reserved
//5 PGN of multi-packet message


    public static int ISO_CONNECTION_CTS = 0xE800;//060416;//
//    1 CTS Group Function Code
//2 Number of frames that can be sent
//3 Number of next frame to be transmitted
//4 NMEA Reserved
//5 PGN of multi-packet message

    public static int ISO_CONNECTION_EOM = 0xE800;//060416;//
//    1 EOM Group Function Code
//2 Total message size, bytes
//3 Total number of frames received
//4 NMEA Reserved
//5 PGN of multi-packet message

    public static int ISO_CONNECTION_BAM = 0xE800;//060416;//
//1 BAM Group Function Code
//2 Total message size, bytes
//3 Total number of frames to be transmitted
//4 NMEA Reserved
//5 PGN of multi-packet message

    public static int ISO_CONNECTION_ABORT = 0xE800;//060416;//
//1 Abort Group Function Code
//2 NMEA Reserved
//3 PGN of multi-packet message

    public static int ISO_CLAIM = 0xE800;//060928;//
//    1 Unique Number (ISO Identity Number)
//2 Manufacturer Code
//3 Device Instance Lower (ISO ECU Instance)
//4 Device Instance Upper (ISO Function Instance)
//5 Device Function (ISO Function)
//6 NMEA Reserved
//7 Device Class
//8 System Instance (ISO Device Class Instance)
//9 Industry Group
//10 NMEA Reserved (ISO Self Configurable)

    public static int ISO_COMMANDED = 0xE800;//065240;//
//1 Unique Number (ISO Identity Number)
//2 Manufacturer Code
//3 Device Instance Lower (ISO ECU Instance)
//4 Device Instance Upper (ISO Function Instance)
//5 Device Function (ISO Function)
//6 NMEA Reserved
//7 Device Class
//8 System Instance (ISO Device Class Instance)
//9 Industry Group
//10 Reserved (ISO Self Configurable)
//11 New Source Address

    public static int NMEA_REQ_GROUP = 0xE800;//126208;//
//    1 Request Group Function Code
//2 Requested PGN
//3 Transmission interval
//4 Transmission interval offset
//5 Number of Pairs of Request Parameters to follow
//6 Field number of first requested parameter
//7 Value of first requested parameter
//8 Variable Number of fields, Field number 6 repeated
//9 Variable Number of fields, Field number 7 repeated

    public static int NMEA_COMMAND_GROUP = 0xE800;//126208;//
//    1 Command Group Function Code
//2 Commanded PGN
//3 Priority Setting
//4 NMEA Reserved
//5 Number of Pairs of Commanded Parameters to follow
//6 Field number of first commanded parameter
//7 Value of first command parameter
//8 Variable Number of fields, Field number 6 repeated
//9 Variable Number of fields, Field number 7 repeated

    public static int NMEA_ACK_GROUP = 0xE800;//126208;//
//    1 Acknowledgment Group Function Code
//2 Requested or Commanded PGN # being acknowledged
//3 PGN error code
//4 Transmission Interval / Priority error code
//5 Number of Requested or Commanded Parameters
//6 First parameter error code
//7 Variable Number of fields, Field number 6 repeated

    public static int NMEA_READ_FIELDS = 0xE800;//126208;//
//    1 Complex Request Group Function Code
//2 PGN Number
//3 Manufacturer's Code
//            4 NMEA Reserved
//5 Industry Group
//6 Unique ID
//7 Number of Selection Pairs
//8 Number of Parameter Pairs to be Read
//9 Field Number of First Selection Pair
//10 Field Value of First Selection Pair
//11 12 13 Variable Number of fields, field 9 repeated
//    Variable Number of Fields, field 10 repeated
//    Field Number of First Parameter Pair to be Read
//14 Variable Number of Fields, field 13 repeated

    public static int NMEA_READ_FIELDS_REPLY = 0xE800;//126208;//
//1 Complex Request Group Function Code
//2 PGN Number
//3 Manufacturer's Code
//            4 NMEA Reserved
//5 Industry Group
//6 Unique ID
//7 Number of Selection Pairs
//8 Number of Parameter Pairs to be Read
//9 Field Number of First Selection Pair
//10 Field Value of First Selection Pair
//11 12 13 Variable Number of fields, field 9 repeated
//    Variable Number of Fields, field 10 repeated
//    Field Number of First Parameter Pair to be Read
//14 Field Value of First Parameter Pair to be Read
//15 16 Variable Number of Fields, field 13 repeated
//    Variable Number of Fields, field 14 repeated

    public static int NMEA_WRITE_FIELDS = 0xE800;//126208;//
//    1 Complex Request Group Function Code
//2 PGN Number
//3 Manufacturer's Code
//            4 NMEA Reserved
//5 Industry Group
//6 Unique ID
//7 Number of Selection Pairs
//8 Number of Parameter Pairs to be Written
//9 Field Number of First Selection Pair
//10 Field Value of First Selection Pair
//11 12 13 Variable Number of fields, field 9 repeated
//    Variable Number of Fields, field 10 repeated
//    Field Number of First Parameter Pair to be Written
//14 Field Value of First Parameter Pair to be Written
//15 16 Variable Number of Fields, field 13 repeated
//    Variable Number of Fields, field 14 repeated

    public static int NMEA_WRITE_FIELDS_REPLY = 0xE800;//126208;//
//    1 Complex Request Group Function Code
//2 PGN Number
//3 Manufacturer's Code
//            4 NMEA Reserved
//5 Industry Group
//6 Unique ID
//7 Number of Selection Pairs
//8 Number of Parameter Pairs to be Written
//9 Field Number of First Selection Pair
//10 Field Value of First Selection Pair
//11 12 13 Variable Number of fields, field 9 repeated
//    Variable Number of Fields, field 10 repeated
//    Field Number of First Parameter Pair to be Written
//14 Field Value of First Parameter Pair to be Written
//15 16 Variable Number of Fields, field 13 repeated
//    Variable Number of Fields, field 14 repeated

    public static int PGN_LIST_TX = 0xE800;//126464;//
//1 Transmitted PGN Group Function Code
//2 First PGN supported
//3 Variable Number of fields, Field number 2 repeated

    public static int PGN_LIST_RX = 0xE800;//126464;//
//    1 Received PGN Group Function Code
//2 First PGN supported
//3 Variable Number of fields, Field number 2 repeated

    public static int ALERT = 0xE800;//126983;//
//    1 Alert Type
//2 Alert Category
//3 Alert System
//4 Alert Sub-System
//5 Alert ID
//6 Data Source Network ID NAME
//7 Data Source Instance
//8 Data Source Index / Source
//9 Alert Occurrence Number
//10 Temporary Silence Status
//11 Acknowledge Status
//12 Escalation Status
//13 Temporary Silence Support
//14 Acknowledge Support
//15 Escalation Support
//16 NMEA Reserved
//17 Acknowledge Source Network ID NAME
//18 Trigger Condition
//19 Threshold Status
//20 Alert Priority
//21 Alert State

    public static int ALERT_RESPONSE = 0xE800;//126984;//
//    1 Alert Type
//2 Alert Category
//3 Alert System
//4 Alert Sub-System
//5 Alert ID
//6 Data Source Network ID NAME
//7 Data Source Instance
//8 Data Source Index / Source
//9 Alert Occurrence Number
//10 Acknowledge Source Network ID NAME
//11 Response Command
//12 NMEA Reserved

    public static int ALERT_TEXT = 0xE800;//126985;//
//    1 Alert Type
//2 Alert Category
//3 Alert System
//4 Alert Sub-System
//5 Alert ID
//6 Data Source Network ID NAME
//7 Data Source Instance
//8 Data Source Index / Source
//9 Alert Occurrence Number
//10 Language ID
//11 Alert Text Description
//12 Alert Location Text Description

    public static int ALERT_CONFIG = 0xE800;//126986;//
//1 Alert Type
//2 Alert Category
//3 Alert System
//4 Alert Sub-System
//5 Alert ID
//6 Data Source Network ID NAME
//7 Data Source Instance
//8 Data Source Index / Source
//9 Alert Occurrence Number
//10 Alert Control
//11 User Defined Alert Assignment
//12 NMEA Reserved
//13 Reactivation Period
//14 Temporary Silence Period
//15 Escalation Period

    public static int ALERT_THRESHOLD = 0xE800;//126987;//
//    1 Alert Type
//2 Alert Category
//3 Alert System
//4 Alert Sub-System
//5 Alert ID
//6 Data Source Network ID NAME
//7 Data Source Instance
//8 Data Source Index / Source
//9 Alert Occurrence Number
//10 Total Number of Threshold Parameters
//11 Parameter Number
//12 Trigger Method
//13 Threshold Data Format
//14 Threshold Level
//15 Fields 11 to 14 Repeat as necessary

    public static int ALERT_VALUE = 0xE800;//126988;//
//    1 Alert Type
//2 Alert Category
//3 Alert System
//4 Alert Sub-System
//5 Alert ID
//6 Data Source Network ID NAME
//7 Data Source Instance
//8 Data Source Index / Source
//9 Alert Occurrence Number
//10 Total Number of Value Parameters
//11 Value Parameter Number
//12 Value Data Format
//13 Value Data
//14 Fields 11 to 13 Repeat as necessary

    public static int SYSTEM_TIME = 0xE800;//126992;//
//    1 Sequence ID
//2 Source
//3 NMEA Reserved
//4 Date
//5 Time

    public static int HEARTBEAT = 0xE800;//126993;//
//    1 Update Rate
//2 Heartbeat Sequence Counter
//3 Class 1 CAN Controller State
//4 Class 2 Second CAN Controller State
//5 Equipment Status
//6 NMEA Reserved

    public static int PRODUCT_INFORMATION = 0xE800;//126996;//
//    1 NMEA Network Message Database Version
//2 NMEA Manufacturer's Product Code
//            3 Manufacturer's Model ID
//            4 Manufacturer's Software Version Code
//            5 Manufacturer's Model Version
//            6 Manufacturer's Model Serial Code
//            7 NMEA 2000 Certification Level
//8 Load Equivalency

    public static int CONFIG_INFORMATION = 0xE800;//126998;//
//    1 Installation Description, Field 1
//            2 Installation Description, Field 2
//            3 Manufacturer Information, Field 3

    public static int MAN_OVBD = 0xE800;//127233;//
//    1 Sequence ID
//2 MOB Emitter ID
//3 Man Overboard (MOB) Status
//4 NMEA Reserved
//5 UTC Time of MOB Activation
//6 Position Source
//7 NMEA Reserved
//8 UTC Date of Position
//9 UTC Time of Position
//10 Latitude
//11 Longitude
//12 Course over ground Reference
//13 NMEA Reserved
//14 Course over ground
//15 Speed over ground
//16 MMSI of vessel of Origin
//17 MOB Emitter Battery Status
//18 NMEA Reserved

    public static int HEADING_CONTROL = 0xE800;//127237;//
//    1 Rudder Limit Exceeded
//2 Off-Heading Limit Exceeded
//3 Off-Track Limit Exceeded
//4 Override
//5 Steering Mode
//6 Turn Mode
//7 Heading Reference
//8 NMEA Reserved
//9 Commanded Rudder Direction
//10 Commanded Rudder Angle
//11 Heading-To-Steer (Course)
//12 Track
//13 Rudder Limit
//14 Off-Heading Limit
//15 Radius of Turn Order
//16 Rate of Turn Order
//17 Off-Track Limit
//18 Vessel Heading

    public static int RUDDER = 0xE800;//127245;//
//    1 Rudder Instance
//2 Direction Order
//3 NMEA Reserved
//4 Angle Order
//5 Position
//6 NMEA Reserved

    public static int VESSEL_HEADING = 0xE800;//127250;//
//    1 Sequence ID
//2 Heading Sensor Reading
//3 Deviation
//4 Variation
//5 Heading Sensor Reference
//6 NMEA Reserved

    public static int RATE_OF_TURN = 0xE800;//127251;//
//    1 Sequence ID
//2 Rate of Turn
//3 NMEA Reserved

    public static int HEAVE = 0xE800;//127252;//
//    1 Sequence ID
//2 Heave
//3 Delay
//4 Delay Source
//5 NMEA Reserved

    public static int ALTITUDE = 0xE800;//127257;//
//    1 Sequence ID
//2 Yaw
//3 Pitch
//4 Roll
//5 NMEA Reserved

    public static int MAGNETIC_VARIATION = 0xE800;//127258;//
//    1 Sequence ID
//2 Variation Source
//3 NMEA Reserved
//4 Age of Service (Date)
//5 Variation
//6 NMEA Reserved

    public static int ENGINE_PARAMS = 0xE800;//127488;//
//    1 Engine Instance
//2 Engine Speed
//3 Engine Boost Pressure
//4 Engine tilt/trim
//5 NMEA Reserved

    public static int ENGINE_PARAMS_DYNAMIC = 0xE800;//127489;//
//    1 Engine instance
//2 Engine oil pressure
//3 Engine oil temp.
//4 Engine temp.
//            5 Alternator potential
//6 Fuel rate
//7 Total engine hours
//8 Engine coolant pressure
//9 Fuel Pressure
//10 Not Available
//11 Engine Discrete Status 1
//            12 Engine Discrete Status 2
//            13 Percent Engine Load
//14 Percent Engine Torque

    public static int ELECTRIC_DRIVE_STATUS = 0xE800;//127490;//
//    1 Inverter/Motor Identifier
//2 Operating Mode
//3 NMEA Reserved
//4 Motor Temperature
//5 Inverter Temperature
//6 Coolant Temperature
//7 Gear Temperature
//8 Shaft Torque

    public static int ELECTRIC_STORAGE_STATUS = 0xE800;//127491;//
//    1 Energy Storage Identifier
//2 State of Charge
//3 Time Remaining
//4 Highest Cell Temperature
//5 Lowest Cell Temperature
//6 Average Cell Temperature
//7 Max. Discharge Current
//8 Max. Charge Current
//9 Cooling System Status
//10 Heating System Status

    public static int TRANSMISSION_PARAMS_DYNAMIC = 0xE800;//127493;//
//    1 Transmission instance
//2 Transmission Gear
//3 NMEA Reserved
//4 Transmission oil pressure
//5 Transmission oil temperature
//6 Transmission Discrete Status
//7 NMEA Reserved

    public static int ELECTRIC_DRIVE_INFO = 0xE800;//127494;//
//    1 Inverter/Motor Identifier
//2 Motor Type
//3 NMEA Reserved
//4 Motor Voltage Rating
//5 Maximum Continuous Motor Power
//6 Maximum Boost Motor Power
//7 Maximum Motor Temperature Rating
//8 Rated Motor Speed
//9 Maximum Controller Temperature Rating
//10 Motor Shaft Torque Rating
//11 Motor DC-Voltage Derating Threshold
//12 Motor DC-Voltage Cut Off Threshold
//13 Drive/Motor Hours

    public static int ELECTRIC_STORAGE_INFO = 0xE800;//127495;//
//    1 Energy Storage Identifier
//2 Energy Storage Mode
//3 NMEA Reserved
//4 Storage Chemistry/Conversion
//5 Maximum Temperature Derating
//6 Maximum Temperature Shut Off
//7 Minimum Temperature Derating
//8 Minimum Temperature Shut Off
//9 Usable Battery Energy
//10 State of Health
//11 Battery Cycle Counter
//12 Battery Full Status
//13 Battery Empty Status
//14 NMEA Reserved
//15 Maximum Charge (SOC)
//16 Minimum Discharge (SOC)

    public static int TRIP_FUEL_CONSUMPTION_VESSEL = 0xE800;//127496;//
//    1 Time to Empty
//2 Distance to Empty /Fuel Range
//3 Estimated Fuel Remaining
//4 Trip Run Time

    public static int TRIP_FUEL_CONSUMPTION_ENGINE = 0xE800;//127497;//
//    1 Engine instance
//2 Trip fuel used
//3 Fuel Rate, Average
//4 Fuel Rate, Economy
//5 Instantaneous Fuel Economy

    public static int ENGINE_PARAMS_STATIC = 0xE800;//127498;//
//    1 Engine instance
//2 Rated engine speed
//3 VIN
//4 Software ID

    public static int LOAD_CONTROLLER_CONN_STATE = 0xE800;//127500;//
//    1 Sequence ID
//2 Connection ID
//3 State
//4 Status
//5 Operational Status & Control
//6 PWM Duty Cycle
//7 TimeON
//8 TimeOFF

    public static int SWITCH_BANK_STATUS = 0xE800;//127501;//
//1 Binary Device Bank Instance
//2 Status 1
//            3 Status 2
//            4 Status 3
//            5 Status 4
//            6 Status 5
//            7 Status 6
//            8 Status 7
//            9 Status 8
//            10 Status 9
//            11 Status 10
//            12 Status 11
//            13 Status 12
//            14 Status 13
//            15 Status 14
//            16 Status 15
//            17 Status 16
//            18 Status 17
//            19 Status 18
//            20 Status 19
//            21 Status 20
//            22 Status 21
//            23 Status 22
//            24 Status 23
//            25 Status 24
//            26 Status 25
//            27 Status 26
//            28 Status 27
//            29 Status 28

    public static int SWITCH_BANK_CONTROL = 0xE800;//127502;//
//    1 Switch Bank Instance
//2 Switch 1
//            3 Switch 2
//            4 Switch 3
//            5 Switch 4
//            6 Switch 5
//            7 Switch 6
//            8 Switch 7
//            9 Switch 8
//            10 Switch 9
//            11 Switch 10
//            12 Switch 11
//            13 Switch 12
//            14 Switch 13
//            15 Switch 14
//            16 Switch 15
//            17 Switch 16
//            18 Switch 17
//            19 Switch 18
//            20 Switch 19
//            21 Switch 20
//            22 Switch 21
//            23 Switch 22
//            24 Switch 23
//            25 Switch 24
//            26 Switch 25
//            27 Switch 26
//            28 Switch 27
//            29 Switch 28

    public static int AC_INPUT_STATUS = 0xE800;//127503;//
//    1 AC Instance
//2 Number of Lines
//3 Line
//4 Acceptability
//5 NMEA Reserved
//6 Voltage
//7 Current
//8 Frequency
//9 Breaker Size
//10 Real Power
//11 Reactive Power
//12 Power Factor

    public static int AC_OUTPUT_STATUS = 0xE800;//127504;//
//    1 AC Instance
//2 Number of lines
//3 Line
//4 Waveform
//5 NMEA Reserved
//6 Voltage
//7 Current
//8 Frequency
//9 Breaker Size
//10 Real Power
//11 Reactive Power
//12 Power Factor

    public static int FLUID_LEVEL = 0xE800;//127505;//
//    1 Fluid Instance
//2 Fluid Type
//3 Fluid Level
//4 Tank Capacity
//5 NMEA Reserved

    public static int DC_DETAILED_STATUS = 0xE800;//127506;//
//    1 Sequence ID
//2 DC Instance
//3 DC Type
//4 State of Charge
//5 State of Health
//6 Time Remaining
//7 Ripple Voltage
//8 Amp Hours

    public static int CHARGER_STATUS = 0xE800;//127507;//
//    1 Charger Instance
//2 Battery Instance
//3 Operating State
//4 Charge Mode
//5 Charger Enable/Disable
//6 Equalization Pending
//7 NMEA Reserved
//8 Equalization Time Remaining

    public static int BATTERY_STATUS = 0xE800;//127508;//
//    1 Battery Instance
//2 Battery Voltage
//3 Battery Current
//4 Battery Case Temperature
//5 Sequence ID

    public static int INVERTER_STATUS = 0xE800;//127509;//
//    1 Inverter Instance
//2 AC Instance
//3 DC Instance
//4 Operating State
//5 Inverter Enable/Disable
//6 NMEA Reserved

    public static int CHARGER_CONFIG_STATUS = 0xE800;//127510;//
//    1 Charger Instance
//2 Battery Instance
//3 Charger Enable/Disable
//4 NMEA Reserved
//5 Charge Current Limit
//6 Charging Algorithm
//7 Charger Mode
//8 Estimated Battery Temp - When No Sensor Present
//9 Equalize One Time Enable/Disable
//10 Over Charge Enable/Disable
//11 Equalize Time


}
