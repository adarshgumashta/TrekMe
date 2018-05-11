package com.peterlaurence.trekadvisor.menu.mapcreate.providers.ign

import android.app.Fragment
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peterlaurence.trekadvisor.R
import com.peterlaurence.trekadvisor.core.mapsource.MapSourceLoader
import com.peterlaurence.trekadvisor.core.providers.BitmapProviderIgn
import com.peterlaurence.trekadvisor.menu.mapview.TileViewExtended
import com.qozix.tileview.TileView
import com.qozix.tileview.widgets.ZoomPanLayout

/**
 * Displays [IGN](https://www.geoportail.gouv.fr/carte) using a [TileView] and a specific
 * [BitmapProviderIgn]. <br>
 * See the documentation of their
 * [WMTS service](https://geoservices.ign.fr/documentation/geoservices/wmts.html). A `GetCapabilities`
 * request reveals that each level is square area. Here is an example for level 18 :
 * ```
 * <TileMatrix>
 *   <ows:Identifier>18</ows:Identifier>
 *   <ScaleDenominator>2132.7295838497840572</ScaleDenominator>
 *   <TopLeftCorner>
 *     -20037508.3427892476320267 20037508.3427892476320267
 *   </TopLeftCorner>
 *   <TileWidth>256</TileWidth>
 *   <TileHeight>256</TileHeight>
 *   <MatrixWidth>262144</MatrixWidth>
 *   <MatrixHeight>262144</MatrixHeight>
 * </TileMatrix>
 * ```
 * This level correspond to a 256 * 262144 = 67108864 px wide and height area.
 * The `TopLeftCorner` corner contains the WebMercator coordinates. The bottom right corner has
 * implicitly the opposite coordinates.
 */
class IgnViewFragment : Fragment() {
    private lateinit var rootView: ConstraintLayout
    private lateinit var tileView: TileViewExtended

    /* Size of level 18 */
    private val mapSize = 67108864
    private val highestLevel = 18

    private val tileSize = 256
    private val x0 = -20037508.3427892476320267
    private val y0 = -x0
    private val x1 = -x0
    private val y1 = x0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_ign_view, container, false) as ConstraintLayout

        addTileView()
        return rootView
    }

    private fun addTileView() {
        val tileView = TileViewExtended(this.context)

        /* IGN wmts maps are square */
        tileView.setSize(mapSize, mapSize)

        /* We will display levels 1..18 */
        val levelCount = highestLevel
        val minScale = 1 / Math.pow(2.0, (levelCount - 1).toDouble()).toFloat()

        /* Scale limits */
        tileView.setScaleLimits(minScale, 1f)

        /* Starting scale */
        tileView.scale = minScale

        /* DetailLevel definition */
        for (level in 0 until levelCount) {
            /* Calculate each level scale for best precision */
            val scale = 1 / Math.pow(2.0, (levelCount - level - 1).toDouble()).toFloat()

            tileView.addDetailLevel(scale, level + 1, tileSize, tileSize)
        }

        /* Allow the scale to be no less to see the entire map */
        tileView.setMinimumScaleMode(ZoomPanLayout.MinimumScaleMode.FIT)

        /* Render while panning */
        tileView.setShouldRenderWhilePanning(true)

        /* Map calibration */
        setTileViewBounds(tileView)

        /* The BitmapProvider */
        val ignCredentials = MapSourceLoader.getIGNCredentials()!!
        tileView.setBitmapProvider(BitmapProviderIgn(ignCredentials, context))

        /* Add the view */
        setTileView(tileView)
    }

    private fun setTileViewBounds(tileView: TileView) {
        tileView.defineBounds(x0, y0, x1, y1)
    }

    private fun setTileView(tileView: TileViewExtended) {
        this.tileView = tileView
        this.tileView.id = R.id.tileview_ign_id
        this.tileView.isSaveEnabled = true
        rootView.addView(tileView, 0)
    }
}
