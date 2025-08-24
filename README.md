# Zip To It!
## Zip across the lands in style!
### Made for ModFest Toybox!

Use a Zippy Sticky Hand to zip and stick to walls! Upon sticking to a wall, you can jump for some extra height, sneak to simply drop, or zip again to another wall!

You can sneak while zipping to break the sticky hand and cancel the zip. This may be helpful if you get stuck, or want to get some extra height while zipping upwards.

By default, Zippy Sticky hands can zip up to 48 blocks away from your current position. Upon zipping, it reduces fall damage, but doesn't fully negate it!

*Note: Zippy Sticky Hands are supposed to be frog themed, but there isn't a lot of material to work with! Just use your imagination a little ;)*

## Crafting
Zippy Sticky Hands can be crafted by combining:
- A slime ball
- A lead
- Any froglight  
Depending on the froglight you use, you'll get a different color Zippy Sticky Hand. From there, you can use any colored Zippy Sticky Hand and a dye to change its color.

## Gamerules

`allowZipcastDuringZipcast` - If enabled, players will be able to use their Zippy Sticky Hands while actively zipping to another location. True by default.

## Custom/Supported Item Components

### Vanilla How-To's
You can give items with specific components by `/give @s <item>[<component identifier>={"<field name>":<field value>}]`. It's basically just entering NBT or json straight into the item.

Example of the cooldown component with an integer of 20. The "seconds" field is unique to the cooldown component - you can find these by either finding a wiki, or viewing their Codec code, as it's just the codec entry.
`/give @s minecraft:ender_pearl[minecraft:use_cooldown={"seconds":20}]`
_Note: The "seconds" field is actually float, not an integer_

You can add more fields by adding a comma after the previous fields' value, and more components by adding a comma after the previous components' ending "}" bracket.

I'm assuming most Vanilla components have each field be required, as that's the case with particles.

Floats can be defined by adding an `f` after the number, or just by adding a decimal point.
Vector3f's can be defined using `[<0f-1f>, <0f-1f>, <0f,1f>]`

### Zipcaster Item Component
`ziptoit:zipcaster` has a few fields for customizing a Zippy Sticky Hand. All the fields are optional!

- `zip_range` (int - default: 48) - Determines the amount of blocks a Zippy Sticky Hand can zip from.
- `zip_speed` (float - default: 2.25f) - Determines the speed which a player will zip with; there is no relative measurement for this speed, other than bigger number means faster zipcaster. **NOTE: ** Faster speeds are unsupported, and will cause issues sticking to walls if too high!
- `max_zips` (int - default: -1) - The maximum amount of zips a Zippy Sticky Hand can make without breaking. Use "-1" for infinite.
- `zipcast_color` (ZipcastColor) - The colors used for the Zippy Sticky Hand. Default is the Yellow Zippy Sticky Hand's colors(despite the actual Zippy Sticky Hand item)

ZipcastColor is a custom object which contains colors for the Zippy Sticky Hand to use. Each field is required!

- `color` (Vector3f or int) - The main color which will be seen when zipping to a location.
- `altColor` (Vector3f or int) - The alternative color which will be seen when zipping to a location. Used for a gradient with the "color" on the main zipping line, and as a rarer color for various colored particles(e.g. the speed lines).
- `brightColor` (Vector3f or int) - The starting color(for like, 7 ticks) of the zip line when zipping to a location.
- `previewColor` (Vector3f or int) - The color shown when aiming a Zippy Sticky Hand.

### Give Command Examples
You can combine and change the values for each example - none of these examples should conflict with each other.

#### Max Zip Uses & Cooldowns

Max zips of 3 (no cooldown)
`/give @s ziptoit:yellow_zippy_hand[ziptoit:zipcaster={"max_zips":3}]`

Cooldown of 5 seconds (no zip limit)
`/give @s ziptoit:yellow_zippy_hand[minecraft:use_cooldown={"seconds":5}]`
The "seconds" value is a float to allow for specific tick times!

Max zips of 3 with a cooldown of 5 seconds.
`/give @s ziptoit:yellow_zippy_hand[ziptoit:zipcaster={"max_zips":3}, minecraft:use_cooldown={"seconds":5}]`

#### Zip Range & Speed

Zip range of 16
`/give @s ziptoit:yellow_zippy_hand[ziptoit:zipcaster={"zip_range":16}]`

Zip speed of 5f
`/give @s ziptoit:yellow_zippy_hand[ziptoit:zipcaster={"zip_speed":5f}]`
The speed value has no relationship to any form of measurement, other than higher values means more speed.

Zip range of 32 and speed of 1f
`/give @s ziptoit:yellow_zippy_hand[ziptoit:zipcaster={"zip_range":32, "zip_speed":1f}]`

#### Zip Colors
Custom Colors Example with:
- Red as main color
- Green as alt color
- Blue as bright color
- White as preview color
`/give @s ziptoit:yellow_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[1f, 0f, 0f], "altColor":[0f, 1f, 0f], "brightColor":[0f, 0f, 1f], "previewColor":[1f, 1f, 1f]}}]`

Notes: 
- Don't forget the extra "{}" brackets for the field value!
- Vector3fs or integers(in theory) can be used to define the colors. A Vector3f can be defined using `[<0f-1f>, <0f-1f>, <0f,1f>]`
- Custom colors will be displayed in the Zippy Sticky Hand's tooltip!

The actual Zippy Sticky Hand item doesn't affect the colors, as they all default to yellow unless grabbed from the creative inventory.

Here is a list of all the default colors, either as integers for shorter copy pasting, or as Vector3f's for easier modifying. It surprisingly only took me an hour to get this list.
##### Defaults (Integers)
Red: `/give @s ziptoit:red_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-1376244, "altColor":-262031, "brightColor":-9253, "previewColor":-65536}}]`  
Orange: `/give @s ziptoit:orange_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-20215, "altColor":-233216, "brightColor":-5167, "previewColor":-26624}}]`  
Yellow: `/give @s ziptoit:yellow_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-334764, "altColor":-154561, "brightColor":-198700, "previewColor":-256}}]`  
Lime: `/give @s ziptoit:lime_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-8259541, "altColor":-11929540, "brightColor":-1638444, "previewColor":-6750464}}]`  
Green: `/give @s ziptoit:green_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-13895610, "altColor":-12781464, "brightColor":-2818082, "previewColor":-16711936}}]`  
Blue: `/give @s ziptoit:blue_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-13925128, "altColor":-16750081, "brightColor":-2496260, "previewColor":-16776961}}]`  
Cyan: `/give @s ziptoit:cyan_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-12255233, "altColor":-12517403, "brightColor":-2491141, "previewColor":-16711681}}]`  
Light blue: `/give @s ziptoit:light_blue_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-6686977, "altColor":-9905921, "brightColor":-2494468, "previewColor":-9110785}}]`  
Pink: `/give @s ziptoit:pink_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-24321, "altColor":-820486, "brightColor":-596481, "previewColor":-35597}}]`  
Magenta: `/give @s ziptoit:magenta_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-46873, "altColor":-56640, "brightColor":-213272, "previewColor":-49423}}]`  
Purple: `/give @s ziptoit:purple_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-6994433, "altColor":-9231617, "brightColor":-1122305, "previewColor":-6815489}}]`  
White: `/give @s ziptoit:white_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-267268, "altColor":-1647641, "brightColor":-143361, "previewColor":-1}}]`  
Light gray: `/give @s ziptoit:light_gray_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-1380369, "altColor":-6117465, "brightColor":-2424833, "previewColor":-3552823}}]`  
Gray: `/give @s ziptoit:gray_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-1380369, "altColor":-6117465, "brightColor":-2424833, "previewColor":-3552823}}]`  
Brown: `/give @s ziptoit:brown_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-10401733, "altColor":-10141659, "brightColor":-4943749, "previewColor":-13232895}}]`  
Black: `/give @s ziptoit:black_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":-14606039, "altColor":-15921903, "brightColor":-10790022, "previewColor":-16777216}}]`  

##### Defaults (Vector3f's)
Red: `/give @s ziptoit:red_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.92156863, 0.0, 0.047058824], "altColor":[0.9882353, 0.0, 0.44313726], "brightColor":[1.0, 0.85882354, 0.85882354], "previewColor":[1.0, 0.0, 0.0]}}]`  
Orange: `/give @s ziptoit:orange_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[1.0, 0.69411767, 0.03529412], "altColor":[0.9882353, 0.44313726, 0.0], "brightColor":[1.0, 0.92156863, 0.81960785], "previewColor":[1.0, 0.59607846, 0.0]}}]`  
Yellow: `/give @s ziptoit:yellow_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.98039216, 0.89411765, 0.32941177], "altColor":[0.99215686, 0.6431373, 0.24705882], "brightColor":[0.9882353, 0.96862745, 0.83137256], "previewColor":[1.0, 1.0, 0.0]}}]`  
Lime: `/give @s ziptoit:lime_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.5058824, 0.972549, 0.16862746], "altColor":[0.28627452, 0.972549, 0.23529412], "brightColor":[0.9019608, 1.0, 0.83137256], "previewColor":[0.59607846, 1.0, 0.0]}}]`  
Green: `/give @s ziptoit:green_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.16862746, 0.972549, 0.27450982], "altColor":[0.23529412, 0.972549, 0.40784314], "brightColor":[0.83137256, 1.0, 0.87058824], "previewColor":[0.0, 1.0, 0.0]}}]`  
Blue: `/give @s ziptoit:blue_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.16862746, 0.5176471, 0.972549], "altColor":[0.0, 0.4117647, 1.0], "brightColor":[0.8509804, 0.9098039, 0.9882353], "previewColor":[0.0, 0.0, 1.0]}}]`  
Cyan: `/give @s ziptoit:cyan_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.26666668, 1.0, 1.0], "altColor":[0.2509804, 1.0, 0.8980392], "brightColor":[0.8509804, 0.9882353, 0.9843137], "previewColor":[0.0, 1.0, 1.0]}}]`  
Light blue: `/give @s ziptoit:light_blue_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.6, 0.9647059, 1.0], "altColor":[0.40784314, 0.84705883, 1.0], "brightColor":[0.8509804, 0.9372549, 0.9882353], "previewColor":[0.45490196, 0.98039216, 1.0]}}]`  
Pink: `/give @s ziptoit:pink_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[1.0, 0.627451, 1.0], "altColor":[0.9529412, 0.47843137, 0.98039216], "brightColor":[0.9647059, 0.8980392, 1.0], "previewColor":[1.0, 0.45490196, 0.9529412]}}]`  
Magenta: `/give @s ziptoit:magenta_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[1.0, 0.28235295, 0.90588236], "altColor":[1.0, 0.13333334, 0.7529412], "brightColor":[0.9882353, 0.74509805, 0.9098039], "previewColor":[1.0, 0.24313726, 0.94509804]}}]`  
Purple: `/give @s ziptoit:purple_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.58431375, 0.27058825, 1.0], "altColor":[0.4509804, 0.13333334, 1.0], "brightColor":[0.93333334, 0.8745098, 1.0], "previewColor":[0.59607846, 0.0, 1.0]}}]`  
White: `/give @s ziptoit:white_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.9843137, 0.92156863, 0.9882353], "altColor":[0.9019608, 0.85882354, 0.90588236], "brightColor":[0.99215686, 0.8117647, 1.0], "previewColor":[1.0, 1.0, 1.0]}}]`  
Light gray: `/give @s ziptoit:light_gray_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.91764706, 0.9372549, 0.9372549], "altColor":[0.63529414, 0.654902, 0.654902], "brightColor":[0.85490197, 1.0, 1.0], "previewColor":[0.7882353, 0.7882353, 0.7882353]}}]`  
Gray: `/give @s ziptoit:gray_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.91764706, 0.9372549, 0.9372549], "altColor":[0.63529414, 0.654902, 0.654902], "brightColor":[0.85490197, 1.0, 1.0], "previewColor":[0.7882353, 0.7882353, 0.7882353]}}]`  
Brown: `/give @s ziptoit:brown_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.38039216, 0.28235295, 0.23137255], "altColor":[0.39607844, 0.2509804, 0.14509805], "brightColor":[0.7058824, 0.5647059, 0.48235294], "previewColor":[0.21176471, 0.08235294, 0.003921569]}}]`  
Black: `/give @s ziptoit:black_zippy_hand[ziptoit:zipcaster={"zipcast_color":{"color":[0.12941177, 0.12941177, 0.16078432], "altColor":[0.050980393, 0.050980393, 0.06666667], "brightColor":[0.35686275, 0.35686275, 0.47843137], "previewColor":[0.0, 0.0, 0.0]}}]`
